# 公開サービス/ツール

## フリーランス向けの税額／収支シミュレータ
* サービスは[こちら](https://netincomesimulator.lifehackaid.com)で不定期に公開しています。
 ![トップページ](https://github.com/craftect/public-sourcecode/assets/131850742/e3f6197d-b3b9-4251-999c-f5354bc1ebff)

### 特徴（開発面） 
* SpringBoot(ThymeLeaf×Mybatis)にて開発。
* [単体テスト](https://github.com/craftect/public-sourcecode/tree/main/java/NetIncomeSimulator/src/test/java/com/lifehackaid/netincomesimulator)だけでなく[画面テスト](https://github.com/craftect/public-sourcecode/tree/main/java/NetIncomeSimulator/seleniumTest)もselenium×Pester(Powershellのテスティングフレームワーク）で自動化。

### 特徴(機能面)
* 認証機能は持たず、URLに付加されるレコードIDによって、過去編集した明細一覧を復元可能。
* 一人別に年間の収益と税額がシミュレート可能。
