package net.evewatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class Character implements Serializable {
	public long characterID;
	public long keyID;
	public String vCode;
	public String characterName;
	
	public Character(long characterID, long keyID, String vCode, String characterName){
		this.characterID = characterID;
		this.keyID = keyID;
		this.vCode = vCode;
		this.characterName = characterName;
	}
	
	public static ArrayList<Character> readAvailableCharacterIDs(Context context){
		ArrayList<Character> characters = new ArrayList<Character>();
		FileInputStream fileIn;
		ObjectInputStream in;
		try{
			fileIn = new FileInputStream(context.getFilesDir()+File.separator+"availableCharacters");
			in = new ObjectInputStream(fileIn);
			characters = (ArrayList<Character>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			
		}
		return characters;
	}
	
	public static ArrayList<Character> readActiveCharacterIDs(Context context){
		ArrayList<Character> characters = new ArrayList<Character>();
		FileInputStream fileIn;
		ObjectInputStream in;
		try{
			fileIn = new FileInputStream(context.getFilesDir()+File.separator+"activeCharacters");
			in = new ObjectInputStream(fileIn);
			characters = (ArrayList<Character>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			
		}
		return characters;
	}
	
	public static void writeAvailableCharacterIDs(ArrayList<Character> characters, Context context) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(context.getFilesDir()+File.separator+"availableCharacters");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(characters);
		out.close();
		fileOut.close();
	}
	
	public static void writeActiveCharacterIDs(ArrayList<Character> characters, Context context) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(context.getFilesDir()+File.separator+"activeCharacters");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(characters);
		out.close();
		fileOut.close();
	}
	
}
