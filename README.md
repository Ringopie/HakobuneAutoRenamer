# HakobuneAutoRenamer

はこぶねオートリネーマー用リポジトリ。

## これは何ですか？

このソフトは、スマートフォンゲーム「アークナイツ」のリザルトスクリーンショットから文字を抽出してリネームするものになります。<br>
<br>
このソフトを使うと、次のようなファイル名に画像ファイルのリネームを行います。<br>
「リージョン名-ステージ名.png」「リージョン名-ステージ名-Hard.png」<br>
例: 「JP-SL-3」「JP-SL-EX-3-Hard.png」<br>
<br>
フォルダに散らかったスクリーンショットを整理する際にご活用ください。<br>
まだ完全版ではないので、機能も少なく精度も低いとは思いますが良ければご利用ください。<br>
※まだサイドストーリーとオムニバスストーリーにしか対応していません。<br>

## 使い方

1. launch.batを起動して、アプリケーションを起動します。<br>
2. [画像を開く]を押して、リネームしたい画像を開きます。<br>この時、png以外を開きたい場合、ファイルのタイプは[すべてのファイル]を選んでください。<br>また、複数の画像を選択することも可能です。
3. 選択した画像のプレビュー、ステージ名読み取り結果が表示されるので、読み取りが正しく行われているかを確認し、必要であれば訂正します。
4. 上にあるラジオボタンで、遊んでいるリージョンを選択します。<br>例えば、日本版でプレイしているのであればJPを選択してください。<br>保存の際に、リージョン名が先頭につきます。
5. 保存したくない画像はチェックを外すことで、一斉保存の対象外になります。(任意)
6. 強襲のクリアリザルトの場合、チェックを入れることで保存の際に「-Hard.png」が末尾につきます。(任意)
7. [保存]を押して、保存先となるディレクトリを選んでください。

## 動作要件

・Java 17<br>
https://www.oracle.com/jp/java/technologies/downloads/#jdk17-windows<br>
※JREを同封できなかったため、お手数をおかけいたしますがダウンロードをお願いします。<br>
・Microsoft Visual C++ 再頒布可能パッケージ<br>
https://learn.microsoft.com/ja-jp/cpp/windows/latest-supported-vc-redist?view=msvc-170<br>
※Windowsの場合、MacとLinuxはbrewやapt等でTesseractをインストールしてください。<br>

## Pull RequestやIssueについて

開発途上のため、気軽に投げてください。

## 使用している技術

・Tess4J<br>
<b>Tesseract</b>というOCRが可能な技術があったため、これのJava対応のものを使用しました。

## ライセンス

このプロジェクトはApache License 2.0で公開されています。<br>
また、プロジェクトには異なるライセンスで提供されているサードパーティライブラリも含まれています。<br>
詳細については、THIRD_PARTY_LICENSE.txtファイルを参照してください。<br>
<br>
本プロジェクトを使用、変更、または配布する際は、Apache License 2.0および含まれる各ライブラリのライセンス条項を遵守した上で、必要であれば以下のコピーライト表記をご利用ください。<br>
<code>Copyright © 2024 Ringopie All rights reserved.</code>

## 免責

本アプリケーションのデータの著作権は「HYPERGRYPH」様、「Yostar」様に帰属します。<br>
本アプリケーションを使用したことによって生じたすべての損害や不具合等に関しては、一切の責任を負いません。<br>
各自の責任においてご使用ください。<br>

## 今後のアップデート予定

・AI学習によるOCRの最適化<br>
・メインストーリーや各種コンテンツへのリザルト対応<br>
・動作の安定化(現在やや不安定)

## アップデート履歴

・2024/04 製作開始<br>
・2024/06/30 ver. 1.00リリース
