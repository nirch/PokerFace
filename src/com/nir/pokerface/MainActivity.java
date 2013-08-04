package com.nir.pokerface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// UI Controls
	private List<TableRow> tableRows = new ArrayList<TableRow>();
	private List<TextView> playerNameTextViews = new ArrayList<TextView>();
	private List<CheckBox> playingCheckBoxes = new ArrayList<CheckBox>();
	private List<Switch> signSwitches = new ArrayList<Switch>();
	private List<EditText> scoreEditTexts = new ArrayList<EditText>();
	
	List<Player> players;
	private double balance = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Constants used in this method
		LayoutParams matchParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LayoutParams wrapParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
	
		// Creating a linear layout (vertical: 1)
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(1);
		linearLayout.setLayoutParams(matchParams);
		
		// Creating the Table Layout
		TableLayout tableLayout = new TableLayout(this);
		linearLayout.addView(tableLayout);

		// Loading the players (adding each player as a row to the view)
		players = PokerFaceManager.loadPlayers(this);
		for (Player player : players) {
			
			// Creating a new row for this player
			TableRow tableRow = new TableRow(this);
			tableRows.add(tableRow);
			
			// Adding the "is playing" checkBox to the table row
			CheckBox playingCheckBox = new CheckBox(this);
			playingCheckBox.setOnClickListener(checkBoxClicked);
			tableRow.addView(playingCheckBox);
			playingCheckBoxes.add(playingCheckBox);

			
			// Adding the label with the player name name to the table row
			TextView playerNameTextView = new TextView(this);
			playerNameTextView.setText(player.getFullName());
			playerNameTextView.setEnabled(false);
			tableRow.addView(playerNameTextView);
			playerNameTextViews.add(playerNameTextView);
			
			// Adding the +/- switch to the table row
			Switch signSwitch = new Switch(this);
			signSwitch.setTextOff("-");
			signSwitch.setTextOn("+");
			signSwitch.setChecked(true);
			signSwitch.setEnabled(false);
			tableRow.addView(signSwitch);
			signSwitches.add(signSwitch);
			
			// Adding the numeric edit text to the table row
			EditText scoreEditText = new EditText(this);
			scoreEditText.setEms(7);
			scoreEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			scoreEditText.setEnabled(false);
			tableRow.addView(scoreEditText);
			scoreEditTexts.add(scoreEditText);
			
			// Adding the table row to the table layout
			tableLayout.addView(tableRow);
		}	


		// Creating the balance button
		Button balanceButton = new Button(this);
		balanceButton.setText("Balance");
		balanceButton.setOnClickListener(balanceClicked);
		balanceButton.setLayoutParams(wrapParams);
		linearLayout.addView(balanceButton);

		
		// Creating the send button
		Button sendButton = new Button(this);
		sendButton.setText("Send");
		sendButton.setOnClickListener(sendClicked);
		sendButton.setLayoutParams(wrapParams);
		linearLayout.addView(sendButton);

		
		// Setting the view to show the Linear Layout
		setContentView(linearLayout);
		
	}

	
	// This event is handled when the user checks or uncheck the "is playing" check box
	// This will disable/enable the corresponding sign switch and score edit text
	private OnClickListener checkBoxClicked = new OnClickListener()
	{
		public void onClick(View view) {
			
			// Getting the index of the check box that was clicked
			// So later I can disable/enable the appropriate views 
	    	CheckBox isPlayingCheckBox = (CheckBox) view;
	    	// *** Maybe I can do it smarter by saving the index on the View (setTag) ***
	    	int currentIndex = playingCheckBoxes.indexOf(isPlayingCheckBox);
	    		    	
	    	signSwitches.get(currentIndex).setEnabled(isPlayingCheckBox.isChecked());
	    	scoreEditTexts.get(currentIndex).setEnabled(isPlayingCheckBox.isChecked());
	    	playerNameTextViews.get(currentIndex).setEnabled(isPlayingCheckBox.isChecked());
		}
	};
	
	
	// This event is handled when the user clicks on the "Send" button
	// The method will generate an email summerizing the scores of the poker game
	private OnClickListener sendClicked = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
			
			String[] recipients = getRecipients();
			String mailSubject = getMailSubject();
			String mailBody = getMailBody();		
			
			emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients); // recipients
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
			startActivity(emailIntent);

		}

		private String getMailBody() {
			// TODO Auto-generated method stub
			String body = "";
			int index = 0;
			for (CheckBox isPlaying : playingCheckBoxes)
			{
				// Checking if this current player is playing
				if (isPlaying.isChecked())
				{
					// Getting the name of this player
					String currentPlayer = players.get(index).getFullName();
					
					// Getting the score of this player
					double currentScore;
					String currentScoreString = scoreEditTexts.get(index).getText().toString();
					if (currentScoreString.equals(""))
					{
						currentScore = 0;
					}
					else
					{
						currentScore = Double.valueOf(currentScoreString);
					}
					
					// Checking the sign (+/-) for this score and manipulating it accordingly 
					if (!signSwitches.get(index).isChecked())
					{
						currentScore = currentScore * -1;
					}
					
					
					body += currentPlayer + ": " + String.valueOf(currentScore) + "\n";
				}
				
				++index;
			}
			
			body += "\n\nThis mail was generated by PokerFace (alpha)";
			
			return body;
		}

		private String getMailSubject() {
				
			String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
			return "Poker Score for " + timeStamp;	
		}

		// Getting the email address of the players
		private String[] getRecipients() {
			
			List<String> playerEmails = new ArrayList<String>();
			
			for (Player player : players) {
				playerEmails.add(player.getEmail());
			}
			
			return playerEmails.toArray(new String[playerEmails.size()]);
		}
	};

	// This event calculates the balance of the scores of the players 
	private OnClickListener balanceClicked = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			// looping over all the "is playing" check boxes to find the playing players
			double balance = 0;
			int index = 0;
			for (CheckBox isPlaying : playingCheckBoxes)
			{
				// Checking if this current player is playing
				if (isPlaying.isChecked())
				{
					// Getting the score of this player
					double currentScore;
					String currentScoreString = scoreEditTexts.get(index).getText().toString();
					
					if (currentScoreString.equals(""))
					{
						currentScore = 0;
					}
					else
					{
						currentScore = Double.valueOf(currentScoreString);
					}
					
					// Checking the sign (+/-) for this score and manipulating it accordingly 
					if (!signSwitches.get(index).isChecked())
					{
						currentScore = currentScore * -1;
					}
					
					balance = balance + currentScore;
				}
				
				++index;
			}
			
			// Printing the balance to the screen
			Toast.makeText(v.getContext(), String.valueOf(balance), Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
