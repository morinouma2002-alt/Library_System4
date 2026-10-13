# 再レビュー: 図書館の貸出システム(修正後)

## 結論

- 実務基準: **約 38 / 100**(前回 約30 → **+8**)
- 初学者基準: **約 78 / 100**(前回 約70 → **+8**)
- コンパイル: `javac -encoding UTF-8` を一時ディレクトリへ出力し **成功(exit 0)**。出力先は削除済み。
- 対話実行: **未確認**(端末出力が崩れるため)。以下の動作はコード読解による判断です。
- 一番大きい前進: 貸出・返却・検索が実装され、`Member` への共通化(`getMaxBooks`/`getCallName` の abstract、テンプレートメソッド)、延滞日数の計算まで入った。
- 一番大きい残課題: ドメインクラス(`Member`/`Library`)が今も `Scanner`/`println` を持つ点、返却・検索・メニューの入力エラーでまだ落ちる点、リポジトリ衛生。
- 新規の重大バグ: 返却で一覧にないIDを入れると NPE(`Member.java` 134行)。

## 前回指摘の検証表

| 前回の指摘 | 判定 | 根拠(行番号) |
|---|---|---|
| Student/Teacher の borrow 重複 | **直った** | `Member.java` 49-98行に一本化。`Student.java`/`Teacher.java` は `getMaxBooks`/`getCallName` のみ。 |
| 親 Member への共通化と abstract getMaxBooks | **直った** | `Member.java` 44, 46行。Student=3 (`Student.java` 24行)、Teacher=5 (`Teacher.java` 15行)。 |
| Scanner/println の分離 | **未修正** | `Member.java` 17行で `Scanner` をフィールド保持、50-139行で入出力。`Library.memberSearch(Scanner)` (`Library.java` 39-77行)も同様。むしろ返却・検索が増えて UI 依存が拡大。 |
| Student の文言バグ(先生/5冊) | **直った** | `Member.java` 57-58行が `getCallName()`/`getMaxBooks()` を使用。 |
| 返却の未実装 | **直った(新バグあり)** | `Member.java` 101-146行、`SystemMain.java` 67行。延滞日数計算あり。ただし下記 N1。 |
| 検索の未実装 | **一部直った** | `SystemMain.java` 68行、`Library.java` 39-77行。ただし下記 N3。「検索」は実質ID指定で著者を見る機能。 |
| nextInt での入力エラー | **一部のみ** | 直った: 種別選択 (`SystemMain.java` 37-41行)、貸出 (`Member.java` 71-74行)、返却 (117-121行)。未修正: メニュー `sc.nextInt()` (`SystemMain.java` 63行)、ID入力 (102, 127行)、検索の `Integer.parseInt` (`Library.java` 59, 68行)。 |
| 貸出失敗時の誤メッセージ | **一部のみ** | 貸出中の本を選ぶと 81-83行の後に `break`、96行の「借りられませんでした」も出て二重表示。存在しないIDは 96行で妥当。再入力ループにはならない。89行は `dueDate + "を借りました"` で文言が不正(「返却期限:」にすべき)。 |
| 会員の同一性が名前のみ | **未修正** | `SystemMain.java` 96, 121行。生徒/先生の種別も見ないため、先生「A」が登録済みだと生徒「A」でも先生が返る。ID は未照合。 |
| 命名 id() → getId | **未修正** | `Member.java` 34行 `id()`。呼び出しも無く未使用。 |
| setMemberList → addMember | **未修正** | `Manager.java` 19行、`SystemMain.java` 105, 129行。 |
| direc | **未修正** | `Book.java` 8, 40行 `direc`/`getDirec`。出力は「監督名」(`Library.java` 70行)で本なのにずれている。 |
| setBorrowed 命名 | **未修正** | `Book.java` 57行(引数なしの set)。`setReturn` (64行)も同様。 |
| パッケージ名 | **未修正** | `Library`/`Manager`/`Member` が大文字始まり。`Library.Library` のまま。 |
| メモ.java | **未修正** | `src/メモ.java` が残存(`setMemberList → addMember` のメモも未対応)。 |
| bin / .gitignore | **未修正** | `.gitignore` なし。`git status` で `bin/*.class` 8件が変更扱い(追跡中)。`.classpath`/`.project`/`.settings` も追跡。コミットは 3件(すべて "first commit")。 |

前回の良い点(`unmodifiableList`、コンストラクタ注入)は維持。`Book` に `dueDate` が追加された(`Book.java` 14行)。

## 新たに発生した問題

- **N1 (高): 返却でNPE** `Member.java` 126-134行。借りていないIDを入力すると `returnBook == null` のまま 134行で `getName()` を呼び落ちる。null チェックと再入力が必要。
- **N2 (中): 未使用コード** `Student.java` 13行 `studentBookList`(未使用、`ArrayList`/`List`/`Book` の import も不要)。`SystemMain.java` 26行 `found` は常に true で意味がなく、`loginLoop` (53行)と二重ループが読みにくい。
- **N3 (中): 検索の問題** `Library.java` 59行 `Integer.parseInt(s)` で数字以外は `NumberFormatException`。範囲 100〜114 が決め打ちで、本を追加すると壊れる(本来は `bookList` から探す)。再帰で呼び続けるため長く使うとスタックが積まれる。入力は `sc.next()` なのでスペース入り題名も扱えない。
- **N4 (中): メニューの switch に default が無い** `SystemMain.java` 64-79行。無効な数字は何も言わず再表示される。また、種別選択で `-1` は終了にならない(`getMember` が null を返し再入力、`SystemMain.java` 75行はメニュー内のみ有効)。
- **N5 (低): 一覧表示** `Member.java` 65行。貸出中は「貸出中」としか出ず、どの本か分からない。
- **N6 (低): `getMember` の先生/生徒分岐が重複** `SystemMain.java` 87-131行。種別だけ違う同一処理。`Teacher`/`Student` 生成を分岐して共通化できる。名前は `sc.next()` なのでスペース入り名前が不可。
- **N7 (低): 業務ロジックが println を返さない** `Member.borrow`/`returnBook` は `void` で結果を返さないため、テストや UI 切替ができない。

## 点数の内訳(実務基準・概算)

| 観点 | 前回 | 今回 | 理由 |
|---|---|---|---|
| OOP設計 | 4/15 | 9/15 | 共通化・abstract・テンプレートメソッドが成立。Member が UI を持つ点で減点 |
| 正しさ・境界 | 4/20 | 8/20 | 機能は揃ったが NPE、二重メッセージ、同一性問題 |
| エラー処理 | 2/10 | 3/10 | 一部入力のみ対応。例外設計なし |
| 命名・パッケージ | 3/10 | 3/10 | 変化なし |
| 関心の分離 | 2/15 | 3/15 | Student/Teacher からは消えたが Member/Library に移っただけ |
| 重複・可読性 | 3/10 | 5/10 | borrow 重複解消。getMember 重複が残る |
| コメント・Javadoc | 2/5 | 2/5 | `★★★` コメント残存 |
| テスト容易性 | 1/10 | 1/10 | 変化なし |
| 衛生 | 1/5 | 1/5 | 変化なし |
| 合計 | 22 | 35 → 動作分を加味して約38 | |

## 次に直すべき改善点 トップ3

1. **返却のNPE修正と、入力処理の一本化**(`Member.java` 126-134行、`SystemMain.java` 63, 102, 127行、`Library.java` 59行)。`nextLine()` + `Integer.parseInt` + try-catch の `readInt()` ヘルパを1つ作り、全入力で使う。ID が一覧に無ければ再入力か中断。
2. **ドメインから Scanner/println を外す**(`Member.java`、`Library.java`)。`Member` は `borrow(Book)`/`returnBook(Book)` が成功/失敗(例外または boolean)を返すだけにし、一覧表示・入力・メッセージは `SystemMain` へ。検索も `Library.findById(int)` を返す形にし、`sc` を Member から削除する。これでテストも書ける。
3. **同一性と命名・衛生**: 会員の照合は ID(と種別)で行う。`id()→getId`、`setMemberList→addMember`、`direc→author`、パッケージを小文字化。`メモ.java` を `.md` に移し、`.gitignore`(`bin/`、`.settings/` 等)を追加して `git rm --cached` で追跡解除。`Student.java` の未使用フィールドと import も削除。

## 総括

前回の最重要指摘(重複・未実装・文言バグ)はほぼ解消され、初学者課題としては完成度が上がっています。実務基準で次の段階に行くには、「UI とロジックの分離」と「入力・異常系の堅牢化」が鍵です。これだけで 55 点前後が見込めます。

(注)本レビューは読み取り専用。コード変更・コミットはしていません。対話実行は未確認です。
