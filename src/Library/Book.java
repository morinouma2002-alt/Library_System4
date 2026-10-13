package Library;

import java.time.LocalDate;

public class Book {

	private String name;
	private String direc;
	private int id;

	private boolean borrowed = false;
	
	//(参照型)宣言しただけなら、nullのまま
	private LocalDate dueDate ;//返却期限を入れる箱

	/*
	 * 
	
	 * LocalDateは　new を作らない型
	 * コンストラクタが外から呼べない作りになっていて、代わりに LocalDate.now() のような専用のメソッドで作ります。
	
	java
	LocalDate d = new LocalDate();   // ✗ エラーになる
	LocalDate d = LocalDate.now();   // ○ こう作る
	
	ArrayList は new ArrayList<>() と書きましたが、LocalDate はそれとは作り方が違う、と覚えておいてください。
	 * 
	 */

	public Book(String name, String direc, int id) {
		this.name = name;
		this.direc = direc;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getDirec() {
		return direc;
	}

	public int getId() {
		return id;
	}

	public boolean getBorrowed() {
		return borrowed;
	}
	
	public LocalDate getDueDate() {
		return dueDate; //期限を取り出す
	}
	
	public void setBorrowed(LocalDate today) {
	    borrowed = true;
	    dueDate = today.plusDays(14);
	}
	
	

	public void setReturn() {
		borrowed = false;
		dueDate=null;//返したら期限を空にする
	}
}
