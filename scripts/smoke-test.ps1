param(
    [string]$BaseUrl = "http://localhost:8080",
    [Parameter(Mandatory = $true)]
    [string]$AdminUsername,
    [Parameter(Mandatory = $true)]
    [string]$AdminPassword,
    [string]$FilePath,
    [long]$ProductId = 1,
    [int]$OrderQuantity = 1
)

$ErrorActionPreference = "Stop"
$BaseUrl = $BaseUrl.TrimEnd("/")
$script:Passed = 0
$script:Failed = 0

function Write-Result {
    param(
        [string]$Name,
        [bool]$Passed,
        [string]$Detail = ""
    )

    if ($Passed) {
        $script:Passed++
        Write-Host "PASS: $Name" -ForegroundColor Green
    } else {
        $script:Failed++
        Write-Host "FAIL: $Name $Detail" -ForegroundColor Red
    }
}

function Invoke-Api {
    param(
        [ValidateSet("GET", "POST", "PUT", "DELETE")]
        [string]$Method,
        [string]$Path,
        [hashtable]$Headers = @{},
        [object]$Body = $null
    )

    $requestHeaders = @{}
    foreach ($key in $Headers.Keys) {
        $requestHeaders[$key] = $Headers[$key]
    }

    $params = @{
        Method          = $Method
        Uri             = "$BaseUrl$Path"
        Headers         = $requestHeaders
        UseBasicParsing = $true
    }

    # PowerShell 7 can return 4xx/5xx responses without disposing their body.
    if ((Get-Command Invoke-WebRequest).Parameters.ContainsKey("SkipHttpErrorCheck")) {
        $params.SkipHttpErrorCheck = $true
    }

    if ($null -ne $Body) {
        $params.ContentType = "application/json"
        $params.Body = ($Body | ConvertTo-Json -Compress)
    }

    try {
        $response = Invoke-WebRequest @params
        return [pscustomobject]@{
            Status = [int]$response.StatusCode
            Body   = $response.Content
        }
    } catch {
        $response = $_.Exception.Response
        if ($null -eq $response) {
            throw
        }

        if ($response.PSObject.Methods.Name -contains "GetResponseStream") {
            $reader = New-Object System.IO.StreamReader(
                $response.GetResponseStream()
            )

            try {
                $bodyText = $reader.ReadToEnd()
            } finally {
                $reader.Dispose()
            }
        } elseif ($null -ne $_.ErrorDetails.Message) {
            $bodyText = $_.ErrorDetails.Message
        } else {
            $bodyText = ""
        }

        return [pscustomobject]@{
            Status = [int]$response.StatusCode
            Body   = $bodyText
        }
    }
}

function Get-Json {
    param([string]$Text)

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return $null
    }

    try {
        return $Text | ConvertFrom-Json
    } catch {
        return $null
    }
}

function Get-Token {
    param(
        [string]$Username,
        [string]$Password
    )

    $response = Invoke-Api `
        -Method "POST" `
        -Path "/auth/login" `
        -Body @{
            username = $Username
            password = $Password
        }

    if ($response.Status -ne 200) {
        throw "login failed for '$Username': HTTP $($response.Status)"
    }

    $json = Get-Json $response.Body
    $token = $json.data.token

    if ([string]::IsNullOrWhiteSpace($token)) {
        throw "login response did not contain data.token for '$Username'"
    }

    return $token
}

function New-AuthHeaders {
    param([string]$Token)

    return @{
        Authorization = "Bearer $Token"
    }
}

Write-Host "Testing $BaseUrl" -ForegroundColor Cyan

# 1. Health check
$health = Invoke-Api -Method "GET" -Path "/actuator/health"
Write-Result `
    -Name "actuator health returns 200" `
    -Passed ($health.Status -eq 200) `
    -Detail "HTTP $($health.Status)"

# 2. Register a unique USER account
$suffix = Get-Date -Format "yyyyMMddHHmmssfff"
$testUsername = "smoke_$suffix"
$testPassword = "Smoke123456"

$register = Invoke-Api `
    -Method "POST" `
    -Path "/auth/register" `
    -Body @{
        username = $testUsername
        password = $testPassword
    }

Write-Result `
    -Name "register temporary USER" `
    -Passed ($register.Status -eq 200) `
    -Detail "HTTP $($register.Status)"

# 3. Login USER and ADMIN
try {
    $userToken = Get-Token `
        -Username $testUsername `
        -Password $testPassword
    Write-Result -Name "login temporary USER" -Passed $true
} catch {
    Write-Result `
        -Name "login temporary USER" `
        -Passed $false `
        -Detail $_.Exception.Message
    $userToken = $null
}

try {
    $adminToken = Get-Token `
        -Username $AdminUsername `
        -Password $AdminPassword
    Write-Result -Name "login ADMIN" -Passed $true
} catch {
    Write-Result `
        -Name "login ADMIN" `
        -Passed $false `
        -Detail $_.Exception.Message
    $adminToken = $null
}

# 4. Authentication and role checks
if ($null -ne $userToken) {
    $userHeaders = New-AuthHeaders $userToken

    $userList = Invoke-Api `
        -Method "GET" `
        -Path "/users?page=1&size=10" `
        -Headers $userHeaders

    Write-Result `
        -Name "USER can query users" `
        -Passed ($userList.Status -eq 200) `
        -Detail "HTTP $($userList.Status)"

    $userCreate = Invoke-Api `
        -Method "POST" `
        -Path "/users" `
        -Headers $userHeaders `
        -Body @{
            id   = 990001
            name = "Smoke User"
            age  = 20
        }

    Write-Result `
        -Name "USER cannot create user" `
        -Passed ($userCreate.Status -eq 403) `
        -Detail "HTTP $($userCreate.Status)"
}

if ($null -ne $adminToken) {
    $adminHeaders = New-AuthHeaders $adminToken

    $adminList = Invoke-Api `
        -Method "GET" `
        -Path "/users?page=1&size=10" `
        -Headers $adminHeaders

    Write-Result `
        -Name "ADMIN can query users" `
        -Passed ($adminList.Status -eq 200) `
        -Detail "HTTP $($adminList.Status)"

    $adminCreate = Invoke-Api `
        -Method "POST" `
        -Path "/users" `
        -Headers $adminHeaders `
        -Body @{
            id   = 990002
            name = "Smoke Admin Created"
            age  = 30
        }

    Write-Result `
        -Name "ADMIN can create user" `
        -Passed ($adminCreate.Status -eq 200) `
        -Detail "HTTP $($adminCreate.Status)"

    $adminUpdate = Invoke-Api `
        -Method "PUT" `
        -Path "/users/990002" `
        -Headers $adminHeaders `
        -Body @{
            name = "Smoke Admin Updated"
            age  = 31
        }

    Write-Result `
        -Name "ADMIN can update user" `
        -Passed ($adminUpdate.Status -eq 200) `
        -Detail "HTTP $($adminUpdate.Status)"

    $adminDelete = Invoke-Api `
        -Method "DELETE" `
        -Path "/users/990002" `
        -Headers $adminHeaders

    Write-Result `
        -Name "ADMIN can delete user" `
        -Passed ($adminDelete.Status -eq 204) `
        -Detail "HTTP $($adminDelete.Status)"

    $adminOrder = Invoke-Api `
        -Method "POST" `
        -Path "/orders" `
        -Headers $adminHeaders `
        -Body @{
            productId = $ProductId
            quantity  = $OrderQuantity
        }

    Write-Result `
        -Name "ADMIN can create order" `
        -Passed ($adminOrder.Status -eq 200) `
        -Detail "HTTP $($adminOrder.Status)"

    if (-not [string]::IsNullOrWhiteSpace($FilePath)) {
        if (-not (Test-Path -LiteralPath $FilePath -PathType Leaf)) {
            Write-Result `
                -Name "file upload input exists" `
                -Passed $false `
                -Detail "file not found: $FilePath"
        } else {
            $upload = Invoke-WebRequest `
                -Method "POST" `
                -Uri "$BaseUrl/files" `
                -Headers $adminHeaders `
                -Form @{
                    file = Get-Item -LiteralPath $FilePath
                }

            Write-Result `
                -Name "ADMIN can upload file" `
                -Passed ($upload.StatusCode -eq 200) `
                -Detail "HTTP $($upload.StatusCode)"
        }
    } else {
        Write-Host "SKIP: file upload (use -FilePath to enable)" `
            -ForegroundColor Yellow
    }
}

# 5. No token must be rejected
$anonymous = Invoke-Api `
    -Method "GET" `
    -Path "/users?page=1&size=10"

Write-Result `
    -Name "anonymous user cannot query users" `
    -Passed ($anonymous.Status -in @(401, 403)) `
    -Detail "HTTP $($anonymous.Status)"

Write-Host ""
Write-Host "Passed: $script:Passed" -ForegroundColor Green
Write-Host "Failed: $script:Failed" -ForegroundColor $(if ($script:Failed -eq 0) { "Green" } else { "Red" })

if ($script:Failed -gt 0) {
    exit 1
}

exit 0
