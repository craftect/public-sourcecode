# 公開サービス/ツール

## フリーランス向けの税額／収支シミュレータ　※[こちら](https://netincomesimulator.lifehackaid.com)で不定期に公開中。
![image](https://github.com/craftect/public-sourcecode/assets/131850742/6f2b32be-f91e-4bf1-996e-895d24cb9dac)

### 特徴（機能面）
* 利便性向上の観点から認証機能は持たないものの、URLに付加されるレコードID(yyyyMMddHHmmss+UUID)によって、過去編集した明細一覧を復元可能。
* 利用者一人ひとりの事情にあわせて年間利益（手取り）と税額がシミュレート可。
* 厚生年金の代替であるiDeCoなどの積み立て資産は自由に使えないため、これを手残りとしてみなすかどうかを判別できるよう価値留保フラグを具備。
 
### 特徴（開発面） 
* SpringBoot(ThymeLeaf×Mybatis)にて開発。
* 右表の集計項目はLinkedListとして保持し、登録順に出力。項目追加する場合はこのLinkedListに要素登録するだけで項目追加が柔軟に可能。
* Web系業務システムの基本機能である明細編集機能。本サービスのソースコードを起点に様々なWeb系業務システムへの展開が可能な設計。
* 変数名やメソッド名は仕様が極力直感的にわかるようにネーミング。（少し長いかもしれません）
* 税率など将来の制度変更の可能性ある業務仕様はハードコーディングせず、設定ファイル（application.yml)から読み込むようにすることで、制度変更に備えた設計としている。
* インフラにはAWSを利用。現在はコスト面からEC2で稼働させているが、将来の拡張性も念頭に、ALB×Fargateの環境も具備。ALB経由とすることでサーバ証明書管理負荷低減やCognito連携による認証機能追加などの拡張性も意識。
* [単体テスト](https://github.com/craftect/public-sourcecode/tree/main/java/NetIncomeSimulator/src/test/java/com/lifehackaid/netincomesimulator)だけでなく[画面テスト](https://github.com/craftect/public-sourcecode/tree/main/java/NetIncomeSimulator/seleniumTest)もselenium×Pester(Powershellのテスティングフレームワーク）で自動化。
``` 実行例
PS C:\\java\NetIncomeSimulator\seleniumTest> .\Selenium.Tests.ps1
Starting Microsoft Edge WebDriver 115.0.1901.203 (a5a2b1779bcfe71f081bc9104cca968d420a89ac) on port 53271

～中略～

DevTools listening on ws://127.0.0.1:53295/devtools/browser/80398d71-6cd0-49b3-b7c5-02802be708ee
Discovery found 6 tests in 5.34s.
Running tests.
Value:家賃
Value:携帯電話使用料
[+] C:\java\NetIncomeSimulator\seleniumTest\Selenium.Tests.ps1 30.76s (22.07s|3.52s)
Tests completed in 30.95s
Tests Passed: 6, Failed: 0, Skipped: 0 NotRun: 0
PS C:\java\NetIncomeSimulator\seleniumTest>
```

* 画面テストはテストケースの前後で画面キャプチャを自動取得できるような仕組みを実装。
* 単体テストはパラメタライズドテスト活用により、テスト仕様と期待値が一目してわかるよう考慮。例えば以下、本サービスの所得税計算用メソッドのテストケースのスニペットに関して、@CsvSourceアノテーション属性における左の値がメソッドへの入力値（所得金額）、右の値が戻り値（所得金額に対応する所得税額）。仕様は国税庁の[所得税速算表](https://www.nta.go.jp/taxes/shiraberu/taxanswer/shotoku/2260.htm)より作成。

 
``` java 
	@ParameterizedTest
	@CsvSource({
		"1949000,97450",
		"1950000,97500",
		"3299000,232400",
		"3300000,232500",
		"6949000,962300",
		"6950000,962500",
		"8999000,1433770",
		"9000000,1434000",
		"17999000,4403670",
		"18000000,4404000",
		"39999000,13203600",
		"40000000,13204000",
		"50000000,17704000",
		"7000000,974000",
		"0, 0", // ゼロの場合
		"-1000, 0" // マイナスの場合
	})
	//@formatter:on 
	void verifyCalcIncomeTaxAmount(long incomeAmount, long expectedValue) {
``` 
 
