# Java enum（列挙型）まとめ

## 1. enumとは？

`enum`（列挙型）は、**取りうる値があらかじめ決まっている場合に使用する型**です。

例えば、図書館システムで会員の状態を管理する場合です。

```java
public enum MemberStatus {
    NORMAL,   // 通常会員
    OVERDUE   // 延滞会員
}
```

この場合、`MemberStatus` が持つ値は `NORMAL` と `OVERDUE` の2種類です。

### enumの特徴

- 取りうる値をあらかじめ定義できる
- タイプミスをコンパイル時に検出できる
- コードの可読性が向上する
- `==` で比較できる
- フィールドやメソッドを定義できる

---

## 2. enumの基本的な使い方

### ① enumを定義する

```java
public enum MemberStatus {
    NORMAL,
    OVERDUE
}
```

### ② enum型の変数を宣言する

```java
MemberStatus status = MemberStatus.NORMAL;
```

値を変更する場合は、次のように書きます。

```java
status = MemberStatus.OVERDUE;
```

通常のクラスとは異なり、`new` を使用して列挙定数を生成することはできません。

### ③ enumを比較する

```java
if (status == MemberStatus.OVERDUE) {
    System.out.println("延滞中です");
}
```

enumの列挙定数は、それぞれ同じインスタンスを参照するため、`==` で比較できます。

---

## 3. なぜenumを使うのか？

### intで管理する場合

```java
int status = 1;
```

これでは `1` が何を意味するのか分かりにくくなります。

### Stringで管理する場合

```java
String status = "NORMAL";

if (status.equals("OVERDEU")) {
    System.out.println("延滞中");
}
```

`OVERDEU` とタイプミスしても、コンパイルエラーになりません。

### enumで管理する場合

```java
MemberStatus status = MemberStatus.NORMAL;

if (status == MemberStatus.OVERDEU) {
    System.out.println("延滞中");
}
```

`OVERDEU` は定義されていないため、コンパイルエラーになります。

**enumを使うことで、不正な値の指定をコンパイル時に検出できます。**

---

## 4. enumは参照型

Javaのenumは特殊なクラスであり、参照型です。

| 比較項目 | boolean | enum | 通常のクラス |
|---|---|---|---|
| 型 | 基本型 | 参照型 | 参照型 |
| null | 不可 | 可能 | 可能 |
| new | 不要 | 使用不可 | 使用可能 |
| 値 | true / false | 定義した定数 | インスタンス |

```java
private MemberStatus status = MemberStatus.NORMAL;
```

このコードは、`MemberStatus` 型のフィールド `status` を宣言し、初期値として `NORMAL` を設定しています。

注意点として、enum型のフィールドは明示的に初期化しなければデフォルトで `null` になります。一方、ローカル変数は使用前に初期化する必要があります。

また、enumは別のクラスを `extends` できませんが、インターフェースの実装は可能です。

---

## 5. 図書館システムでの使用例

図書館システムでは、延滞中の会員が新しい本を借りられないようにします。

### MemberStatus.java

```java
public enum MemberStatus {
    NORMAL,
    OVERDUE
}
```

### Member.java（一部抜粋）

```java
private MemberStatus status = MemberStatus.NORMAL;

// 会員の延滞状態を更新する
public void updateStatus() {

    status = MemberStatus.NORMAL;

    for (Book book : bookList) {

        if (book.getDueDate().isBefore(library.getToday())) {
            status = MemberStatus.OVERDUE;
            break;
        }
    }
}

// 本の貸出処理
public void borrow() {

    updateStatus();

    if (status == MemberStatus.OVERDUE) {
        System.out.println("延滞中の本があるため借りられません");
        return;
    }

    // 以下、貸出処理
}
```

※ `bookList` は現在借りている本の一覧で、`getDueDate()` は返却期限を返す想定です。

### 設計のポイント

会員の状態を固定するのではなく、**借りている本の返却期限と現在の日付を比較して、状態を更新する設計**にしています。

- 延滞している本がない → `NORMAL`
- 1冊でも延滞している → `OVERDUE`
- 延滞中は新しい本を借りられない

このように、enumを使用すると状態管理を分かりやすく表現できます。

---

## 6. 応用：enumにフィールドを持たせる

enumは単に定数を定義するだけでなく、フィールド、コンストラクタ、メソッドを持つことができます。

```java
public enum MemberRank {

    NORMAL(14, 0),
    GOLD(21, 2);

    private final int loanDays;
    private final int extraBooks;

    MemberRank(int loanDays, int extraBooks) {
        this.loanDays = loanDays;
        this.extraBooks = extraBooks;
    }

    public int getLoanDays() {
        return loanDays;
    }

    public int getExtraBooks() {
        return extraBooks;
    }
}
```

### 会員ランクごとの設定

| ランク | 貸出期間 | 追加貸出冊数 |
|---|---|---|
| NORMAL | 14日 | 0冊 |
| GOLD | 21日 | 2冊 |

### 使用例

```java
MemberRank rank = MemberRank.GOLD;

System.out.println(rank.getLoanDays());    // 21
System.out.println(rank.getExtraBooks());  // 2
```

ランクごとの条件をenum自身に持たせることで、呼び出し側で毎回 `if` を書く必要がなくなります。

---

## 7. OOP設計で重要な考え方

**異なる意味を持つ状態は、別々のenumで管理することが重要です。**

例えば、次の2つは別の概念です。

```java
public enum MemberRank {
    NORMAL,
    GOLD
}
```

```java
public enum MemberStatus {
    NORMAL,
    OVERDUE
}
```

`MemberRank` は会員のランクを表し、`MemberStatus` は延滞状態を表します。

Memberクラスでは、それぞれ別のフィールドとして管理できます。

```java
private MemberRank rank = MemberRank.NORMAL;

private MemberStatus status = MemberStatus.NORMAL;
```

これにより、**ゴールド会員でありながら延滞中**という状態も表現できます。

---

## 8. 実務での使用例

enumは、業務システムで決められた種類や状態を管理する際によく使用されます。

| 用途 | enumの例 |
|---|---|
| 注文状態 | ORDERED, SHIPPED, DELIVERED |
| 会員状態 | NORMAL, OVERDUE |
| 会員ランク | NORMAL, GOLD |
| ユーザー権限 | USER, ADMIN |
| 支払い方法 | CREDIT_CARD, BANK_TRANSFER |
| ログレベル | INFO, WARN, ERROR |

特に、**値の種類が決まっていて、それぞれに明確な意味がある場合**に適しています。

---

## 9. まとめ

Javaのenumは、取りうる値を限定して管理するための参照型です。

重要なポイントは次のとおりです。

1. enumは決められた値だけを扱える
2. 列挙定数は `型名.定数名` で参照する
3. 比較には `==` を使用できる
4. タイプミスをコンパイル時に検出できる
5. フィールドやメソッドを持たせることができる
6. 異なる概念の状態は別々のenumで管理する

**enumは単なる定数の集まりではなく、OOPにおける状態や種類を安全かつ明確に表現するための仕組みです。**
