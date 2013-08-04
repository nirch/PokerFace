package com.nir.pokerface;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class PokerFaceManager {
	
	/**
	 * Loading the players from a CSV file
	 * @param context
	 * @return
	 */
	static List<Player> loadPlayers(Context context)
	{
		List<Player> players = new ArrayList<Player>();
		AssetManager assetManager = context.getAssets();
	
		try {
			
			// Opening the players CSV file
			InputStream playersCSV = assetManager.open("players.csv");
			InputStreamReader playersCsvStreamReader = new InputStreamReader(playersCSV);
		    CSVReader playersCsvReader = new CSVReader(playersCsvStreamReader);
		    
		    // throw away the header, and looping over the rows in the CSV file
		    playersCsvReader.readNext();
		    String[] currentRow = playersCsvReader.readNext();
		    while (currentRow != null) {
		        
		    	// Creating a player based on the current row and adding it to the result
		    	Player player = new Player(currentRow[0], currentRow[1], currentRow[2]);
		    	players.add(player);
		    	
		    	// Moving to the next row
		    	currentRow = playersCsvReader.readNext();
		    }
			
		    playersCsvReader.close();

		} catch (IOException e) {
			
			Log.e("loadPlayers", e.getMessage());
			e.printStackTrace();			
		}
				
		return players;
	}

}
