import java.util.*;

import java.io.*;

public class Person {
	public static final Person NO_ONE = new Person("NO_ONE_");
	
	TreeMap <Integer, Integer> preferences;  //x,y refers to match, preference #; so (3,4) refers to person 3 who is the 4th most preferred mate
	private Person match;
	private String name;
	
	
	public Person()
	{
		this.name = "NO_ONE";
		this.preferences = new TreeMap <Integer, Integer>();	
		this.match = NO_ONE;
		
	}
	
	public Person(String name)
	{
		this.name = name;
		this.preferences =  new TreeMap <Integer, Integer>();	
		this.match = NO_ONE;
	}
	
	
	public Person(String name, TreeMap<Integer, Integer> preferences)
	{
		this.name = name;
		this.preferences = new TreeMap <Integer, Integer>(preferences);	
		this.match = NO_ONE;
	}
	
	public TreeMap<Integer, Integer> getPreferences()
	{
		return this.preferences;
	}
	
	public void addPreference(int person, int preference)
	{
		this.preferences.put(person, preference);
	}
	
	public boolean hasPreferences()
	{
		return (this.preferences.size() > 0);
	}
	
	
	public static void main(String[] args)
	{
		Person person = new Person();
		person.addPreference(5,3);
		person.addPreference(4,2);
		person.addPreference(3,4);
		person.addPreference(2,-1);
		person.addPreference(1,5);
		System.out.println(person.getFirstChoice());
	}
	
	public int getFirstChoice()
	{
		//returns the number of the person who is preferred most in this.preferences
		
		ArrayList<Integer> sortedChoices = new ArrayList<Integer>(this.preferences.values()); //collection of preference #s
		
		Collections.sort(sortedChoices);
		
	
		int firstChoice = sortedChoices.get(0); //determine the highest preference (lowest number)
		
		for (int person : this.preferences.keySet()) //look for the key that has the lowest number as its value
		{
			if (this.preferences.get(person) == firstChoice)
			{
				return person;
			}
		}
		
		return -1;
	}
	
	public boolean isEngaged()
	{
		return (!this.match.equals(NO_ONE));
	}
	
	public void engage(Person person)
	{
		this.match = person;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof Person))
			return false;
		
		Person person = (Person)o;
		return (this.name.equals(person.getName()));
		
	}
	
	public Person getMatch()
	{
		return this.match;
	}
	
	public void setFree()
	{
		this.match = NO_ONE;
	}

	
	
	public void remove(int loser)
	{
		//look for key, loser, in this.preferences
		//if found, remove that key/value pair from the map
		if (this.preferences.containsKey(loser))
		{
			this.preferences.remove(loser);
		}
	}
}
