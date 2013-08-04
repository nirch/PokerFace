package com.nir.pokerface;

public class Player {
	
	private String firstName;
	private String lastName;
	private String email;
	
	public Player(String firstName, String lastName, String email) 
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName()
	{
		return firstName + " " + lastName;
	}
	
	@Override
	public String toString() {
		return "Player [firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}
	
}
