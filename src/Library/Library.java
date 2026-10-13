package Library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Library {

	//has-aの関係
	private List<Book> bookList = new ArrayList<>();
	
	private LocalDate today=LocalDate.now();

	//libraryがmemberを管理しているのは、違和感ある
	//private List<Member> memberList = new ArrayList<>();

	public Library() {
		bookList.add(new Book("春", "太郎", 100));
		bookList.add(new Book("夏", "山田", 101));
		bookList.add(new Book("秋", "花子", 102));
		bookList.add(new Book("冬", "先生", 103));
		bookList.add(new Book("春雨", "佐藤", 104));
		bookList.add(new Book("夕焼け", "鈴木", 105));
		bookList.add(new Book("星の旅", "高橋", 106));
		bookList.add(new Book("海の音", "田中", 107));
		bookList.add(new Book("風の道", "伊藤", 108));
		bookList.add(new Book("森の記憶", "渡辺", 109));
		bookList.add(new Book("雪解け", "中村", 110));
		bookList.add(new Book("月明かり", "小林", 111));
		bookList.add(new Book("朝の光", "加藤", 112));
		bookList.add(new Book("夜行列車", "吉田", 113));
		bookList.add(new Book("遠い夏", "山本", 114));

	}
	
	public LocalDate getToday() {
		return today;
	}
	
	public void nextDay() {
		today=today.plusDays(1);
		System.out.println("日付が進みました"+today);
	}
	
	public List<Book> getBookList() {
		return Collections.unmodifiableList(bookList);
	}

	public void memberSearch(Scanner sc) {

		//再帰
		System.out.println("----検索----");
		System.out.println("終了したい場合は、NOと入力してください");

		for (Book book : bookList) {
			System.out.println("番号:" + book.getId() + "/題名:" + book.getName());
		}

		System.out.println("番号を入力してください");

		//trim()メソッドが、意味なくなってるから後で考える
		String s = sc.next().trim();

		if (s.equals("NO")) {
			System.out.println("終了");
			return;
		}

		if (Integer.parseInt(s) < 100 || Integer.parseInt(s) > 114) {
			System.out.println("範囲外です"
					+ "もう一度、入力し直してください");

			memberSearch(sc);
			return;
		}
		
		for (Book book : bookList) {
			if(book.getId()==(Integer.parseInt(s))) {
				
				System.out.println("監督名は"+book.getDirec()+"です");
				break;
			}
		}
		
		memberSearch(sc);

	}

}
