package Member;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import Library.Book;
import Library.Library;

public abstract class Member {

	private String name;
	private int id;
	protected Scanner sc;
	private Library library;

	// ★★★借りた本（先生も生徒も共通）
	private List<Book> bookList = new ArrayList<>();
	
	private MemberStatus status =MemberStatus.NORMAL;

	public Member(String name, int id, Scanner sc, Library library) {
		this.name = name;
		this.id = id;
		this.sc = sc;
		this.library = library;
	}

	public String getName() {
		return name;
	}

	public int id() {
		return id;
	}

	public List<Book> getBookList() {
		return Collections.unmodifiableList(bookList);
	}

	//子クラスが決める
	// 「先生」「生徒」
	protected abstract int getMaxBooks();

	protected abstract String getCallName();

	//borrow()は共通のふるまい
	public void borrow() {
		
		updateStatus();
		if(status==MemberStatus.OVERDUE) {
			System.out.println("延滞中の本があるので借りられない");
			return;
		}

		//テンプレートメソッドパターン
		if (bookList.size() >= getMaxBooks()) {
			System.out.println("もう、借りられない");
			return;
		}

		System.out.println(getCallName() + ",何を借りますか?");
		System.out.println("借りられるのは、" + getMaxBooks() + "冊まで");
		System.out.println("借りたい本の、IDを入力してください");

		List<Book> libraryBooks = library.getBookList();

		for (Book book : libraryBooks) {
			if (book.getBorrowed()) {
				System.out.println("貸出中");
			} else {
				System.out.println(book.getId() + ":" + book.getName());
			}
		}

		while (!sc.hasNextInt()) {
			System.out.println("数値を入力してください");
			sc.next();
		}

		int index = sc.nextInt();

		for (Book book : libraryBooks) {
			if (book.getId() == index) {
				if (book.getBorrowed()) {
					System.out.println("その本は、すでに借りられています"
							+ "やりなおしてください");
					break;
				}

				System.out.println(book.getName() + "を借りました");
				
				book.setBorrowed(library.getToday());
				
				bookList.add(book);//本を貯める
				System.out.println(book.getDueDate() + "を借りました");

				return;
			}

		}

		System.out.println("借りられませんでした");

	}

	//returnも共通のふるまい
	public void returnBook() {

		if (bookList.isEmpty()) {
			System.out.println("借りている、本はありません");
			return;
		}

		System.out.println("あなたが、借りている本です");
		System.out.println("IDを入力してください");
		for (Book book : bookList) {
			System.out.println(book.getId() + ":" + book.getName());
		}

		int indexId;
		while (true) {

			if (!sc.hasNextInt()) {
				System.out.println("数値を入力してください");
				sc.next();
				continue;
			}
			indexId = sc.nextInt();
			break;
		}

		Book returnBook = null;
		for (Book book : bookList) {
			if (book.getId() == indexId) {
				returnBook = book;
				break;
			}
		}
		
		System.out.println(returnBook.getName()+"を返却します");
		
		LocalDate today = library.getToday();                          // 今日
		if (returnBook.getDueDate().isBefore(today)) {              // 期限が今日より前＝延滞
		    long late = ChronoUnit.DAYS.between(returnBook.getDueDate(), today);   // 何日遅れか
		    System.out.println(late + "日遅れています");
		}

		returnBook.setReturn();   // 判定のあとに呼ぶ（先に呼ぶと期限がnullになる）
		
		//抽象メソッドにしなくても、返却はただ減らすだけなので、removeする
		bookList.remove(returnBook);
		
		//延滞客で、本を全部返したら通常客に戻る
		if(status==MemberStatus.OVERDUE&&bookList.isEmpty()) {
			status=MemberStatus.NORMAL;
			System.out.println("通常客にもどれました");
		}
	}
	
	//借りている本を１冊ずつ見て、期限切れが一冊でもあれば延滞客にする
	public void updateStatus() {
		status =MemberStatus.NORMAL;
		
		for(Book book:bookList) {
			
			//期限が今日より前の本が一冊でもあれば延滞客
			if(book.getDueDate().isBefore(library.getToday())) {
				status =MemberStatus.OVERDUE;
			}
		}
	}
	
	public void search() {
		library.memberSearch(sc);
	}

}
