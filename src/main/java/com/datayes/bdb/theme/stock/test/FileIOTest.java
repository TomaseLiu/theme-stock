package com.datayes.bdb.theme.stock.test;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class FileIOTest {

	public void createFolderIfNotExist(String folderPath){
		File fileToBeCreate = new File(folderPath);
		if(!fileToBeCreate.exists()){
			Boolean success = fileToBeCreate.mkdirs();
			if(!success)
				System.out.println("create folder failed!");
		}
	}
	
}
