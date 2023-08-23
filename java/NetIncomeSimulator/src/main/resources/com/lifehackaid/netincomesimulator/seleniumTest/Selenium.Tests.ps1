Set-StrictMode -Version 3.0
# 本スクリプトを実行して、Tests Passed: 5, Failed: 0, Skipped: 0 NotRun: 0のようにPassed以外がゼロならテスト完了。
# テスト実施前にEdgeプロセスを停止しておくことをお勧めします。
# またテスト終了後Edgeプロセスはそのままとなります。適宜結果確認の上、手動停止ください。

# エラー発生時は処理中断。
$ErrorActionPreference = "Stop"

# ドライバ類の読み込み ref:https://note.com/dokoka3568/n/n4df03afcfe26
# 自環境のEdgeバージョンに合致するmsedgedriverをDL（https://developer.microsoft.com/ja-jp/microsoft-edge/tools/webdriver/）
# webdriver.dll入手先　https://www.nuget.org/packages/Selenium.WebDriver
# webdriver.support.dll入手先 https://www.nuget.org/packages/Selenium.Support
# DLしたファイルは必ず、ゾーン識別子情報を削除しておく。（対象ファイルのプロパティ画面を表示。画面下部の「セキュリティ」の欄にある「許可する」をチェック）
Add-Type -Path (Join-Path $PSScriptRoot "lib\WebDriver.dll")
Add-Type -Path (Join-Path $PSScriptRoot "lib\WebDriver.Support.dll")

# Screenshot取得用
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.Windows.Forms

# ScreenShot取得クラスの読み込み
. (Join-Path $PSScriptRoot "CommonScreenShotUtil.ps1")

# オプションの指定
$Options = New-Object OpenQA.Selenium.Edge.EdgeOptions

# Headlessモード利用する際はこちらをコメントアウト
# $Options.AddArgument("headless")

# Edge Driverでサービスを作成
$Service = [OpenQA.Selenium.Edge.EdgeDriverService]::CreateDefaultService((Join-Path $PSScriptRoot "lib"), "msedgedriver.exe")

# ドライバオブジェクトを作成
$driver = New-Object OpenQA.Selenium.Edge.EdgeDriver($Service, $Options)

# 画面の最大化
$driver.Manage().Window.Maximize()
#$driver.Manage().Cookies.DeleteAllCookies();

# ScreenShotオブジェクトの作成と保存先の指定
$screenShotDriver = New-Object CommonScreenShotUtil("nis", (Join-Path $env:userprofile "Desktop"))

# 処理間隔（秒）
$intervalSeconds = 1

# サービスURL
$url = "http://localhost:8080/"
#$url="https://netincomesimulator.lifehackaid.com/"

Describe "NetIncomeSimulator-UITest" {

    It "read(show)" {

        $driver.Navigate().GoToUrl($url)

        $xpath = "/html/body/div[3]/div[1]/table/tbody[16]/tr/td[5]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -Be "10,000"

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)
    }
 
    It "update" {

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[7]/form/button[1]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Click()

        Start-Sleep -Seconds $intervalSeconds

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[3]/input"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Clear()

        Start-Sleep -Seconds $intervalSeconds

        $element.SendKeys("660000")

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

        Start-Sleep -Seconds $intervalSeconds

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[7]/button[1]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Click()        

        Start-Sleep -Seconds $intervalSeconds

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[3]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -Be "660,000"

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

    }

    It "update(invalid)" {

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[7]/form/button[1]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Click()

        Start-Sleep -Seconds $intervalSeconds

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[3]/input"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Clear()

        Start-Sleep -Seconds $intervalSeconds

        $element.SendKeys("test")

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

        Start-Sleep -Seconds $intervalSeconds

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[7]/button[1]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Click()        

        Start-Sleep -Seconds $intervalSeconds

        # メッセージ確認
        $xpath = "/html/body/div[3]/div[1]/table/thead/tr[1]/th[2]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -BeLike "*不正な値が入力されました*"

        $xpath = "/html/body/div[3]/div[1]/table/tbody[1]/tr/td[3]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -Be "660,000"

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

    }    

    It "delete" {

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

        #検証用の当初値を取得
        $xpath = "/html/body/div[3]/div[1]/table/tbody[2]/tr/td[2]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $preservedValue = $element.Text
        Write-Host "Value:$preservedValue"

        #削除ボタンを押下
        $xpath = "/html/body/div[3]/div[1]/table/tbody[2]/tr/td[7]/form/button[2]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Click()

        Start-Sleep -Seconds $intervalSeconds

        # アラートをハンドルするためのオブジェクトを取得
        $alert = $driver.SwitchTo().Alert()
        # アラートのメッセージに対してOKを押す
        $alert.Accept()

        Start-Sleep -Seconds $intervalSeconds

        #当初値から変更されているかチェック
        $xpath = "/html/body/div[3]/div[1]/table/tbody[2]/tr/td[2]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $currentValue = $element.Text
        Write-Host "Value:$currentValue"

        $preservedValue | Should -Not -Be $currentValue

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

    }

    It "create(Add)" {


        # selectBoxの上から3つ目を選択
        $xpath = "/html/body/div[3]/div[1]/table/tfoot/tr/td[1]/select"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $dropdown = [OpenQA.Selenium.Support.UI.SelectElement]($element)
        $dropdown.SelectByIndex(3)

        Start-Sleep -Seconds $intervalSeconds

        # 摘要を入力
        $xpath = "/html/body/div[3]/div[1]/table/tfoot/tr/td[2]/input"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.clear()
        $element.sendKeys("医療費控除")

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

        Start-Sleep -Seconds $intervalSeconds

        # 追加ボタンを押下
        $xpath = "//*[@id='detailTable']/tfoot/tr/td[7]/button"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.click()

        Start-Sleep -Seconds $intervalSeconds

        # 値を検証
        $xpath = "/html/body/div[3]/div[1]/table/tbody[16]/tr/td[5]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -Be "120,000"

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

    }

    It "save" {

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

        # 一覧保存ボタンを押下
        $xpath = "/html/body/div[3]/div[1]/table/thead/tr[1]/th[1]/form/button"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.click()

        Start-Sleep -Seconds $intervalSeconds

        # メッセージ確認
        $xpath = "/html/body/div[3]/div[1]/table/thead/tr[1]/th[2]"
        $element = $driver.FindElement([OpenQA.Selenium.By]::XPath($xpath))
        $element.Text | Should -BeLike "*データを保存しました*"

        $screenShotDriver.createScreenShot($MyInvocation.UnboundArguments[1].CurrentTest.Name)

    }

    $driver.Quit()

}
