package com.mongodb;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoConnection mc = new MongoConnection();

			mc.connect();
			mc.dbase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
