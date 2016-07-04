package edu.kit.student.util;

public class IdGenerator {
	
	private static IdGenerator instance;
	
	private Integer idCount;
	
	private IdGenerator() {
		idCount = 0;
	}
	
	public static IdGenerator getInstance() {
		if(instance == null) {
			instance = new IdGenerator();
		}
		return instance;
	}
	
	public Integer createId() {
		Integer id = idCount;
		idCount++;
		return id;
	}

}
