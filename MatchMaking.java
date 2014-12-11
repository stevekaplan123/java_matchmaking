import java.util.*;
import java.util.regex.Pattern;
import java.lang.Object;
import java.io.*;

public class MatchMaking {
	public static void main(String[] args) throws FileNotFoundException {

		HashMap<Person,Integer> men = new HashMap<Person,Integer>();
		HashMap<Person,Integer> women = new HashMap<Person,Integer>();
		readPreferences(new File("stable.dat"), men, women);  //read in preferences for each man and woman
		
		HashMap<Person,Integer> men_2 = new HashMap<Person,Integer>(men);
		HashMap<Person,Integer> women_2 = new HashMap<Person,Integer>(women);
	
		
		executeAlgorithm(women, men); //execute in favor of women
		printOut(women, men);
		
		executeAlgorithm(men_2, women_2);  //now in favor of man
		printOut(men_2, women_2);
		
		
	}

	public static void printOut(HashMap<Person,Integer> men, HashMap<Person,Integer> women) {
	
	
		double meanMensChoice = 0;
		
		for (Person person : men.keySet()) {
			//for each man, print out his name, his match's name and the number preference for that match
	
			int hisChoiceNum = choiceNumber(person, women); //get the number preference 
			if (hisChoiceNum!=-1) //in other words, he found a mate
			{
				meanMensChoice += hisChoiceNum;
				System.out.println(person.getName()+" "+hisChoiceNum+" "+person.getMatch().getName());
			}
			else			//if he didn't find a mate, print just his name and the name of no one, his match
			{
				System.out.println(person.getName()+" -- "+person.getMatch().getName());

			}
			
		
		}
		

		meanMensChoice /= men.size();
		System.out.println("Average: " +meanMensChoice+"\n\n");

	}
	
	public static int choiceNumber(Person person, HashMap<Person,Integer> mates)
	{
		TreeMap<Integer, Integer> currPref = person.getPreferences();
		Person mate = person.getMatch();
		
		for (int personNumber : currPref.keySet()) //look through all of person's possible matches
												//and find the one who corresponds to his actual mate
												//in mates
		{
			if (mates.containsValue(personNumber)) //potential match..
			{
				if (findPerson(personNumber, mates).equals(mate))  //actually matches so set value and return
				{
					int choiceNumber = currPref.get(personNumber);  
					return choiceNumber;

				}
			}
			
		}
		return -1; //did not find mate
	}

	public static boolean someMenInterested(HashMap<Person,Integer> men)
	{
		//returns true if at least one men is still interested in marriage: not engaged and he is capable of finding a match according
		//to stable marriage algorithm (they still have preferences)
		//multiply his value by -1 if he is no longer interested, 
		//but if he and his match connection is broken, we need to re-allow him to
		//look by multiplying it by -1 again and making his number positive
		
		boolean someInterested = false;
		
		for (Person person : men.keySet())
		{
			if (!person.isEngaged() && person.hasPreferences()) // consider someone not engaged and with preferences 
																//for marriage as someone who is still looking
			{
				someInterested = true;
				if (men.get(person)<0) //if he was previously engaged, re-allow him to look
					men.put(person, men.get(person)*-1); //flips his value from negative to positive
			}
			else
			{
				if  (men.get(person) > 0) //he now has become uninterested, so set his value to negative
				{
					men.put(person, men.get(person)*-1);  //no longer interested, will be ignored from now on
				}
			}
		}
		return someInterested;
	}
	
	public static Person findPerson(int personNumber, HashMap<Person,Integer> people)
	{
		//find the Person in people who's value is equal to personNumber
		for (Person eachPerson : people.keySet()) 
		{
			if (people.get(eachPerson)==personNumber)
			{
				return eachPerson;
			}
				
		}
		return new Person();  //if no people, return a person with no name
	}
	
	public static void executeAlgorithm(HashMap<Person,Integer> men, HashMap<Person,Integer> women)
	{		
		while(someMenInterested(men))  //only keeps looping if there is at least one man still interested in marriage
		{
			for (Person person : men.keySet()) //for each man ...
			{
				int personNumber = men.get(person);
				int thisMan = 0;  //will hold how the preference that the woman he wants to marry wants to marry him

				
				if (personNumber<0)  //person is engaged or not looking, continue the loop
					continue;
				
				int w = person.getFirstChoice(); //the number in women of who the person wants to marry

				Person woman = findPerson(w, women);	//now we need to find who the number corresponds so
												//findPerson() looks through women looking for which woman has the right value indicating
												//the one this person wants to marry
					
				
				if (woman.getPreferences().containsKey(personNumber)) //does she still want to marry the person ("personNumber")
				{
					thisMan = woman.getPreferences().get(personNumber); //determine what preference this woman has to marry the man
																//who she is now engaged to marry, if he's not on her list
				}
				else
				{
					//this man has been deleted from her list!
					//he should remove her from his list and try again next time
					person.remove(w);
					continue;
				}
				
				if (woman.isEngaged())  //if the woman he wants is married, set her and her former beloved to not being engaged
				{
					woman.getMatch().setFree();
					woman.setFree();
				}
				
				
				person.engage(woman);  //now actually engage the current man and his desired woman
				woman.engage(person);

				
				//now we need to delete the men from the woman's list who are less preferred than this new man
				//and we need to delete the woman from each of those less preferred men's list of marriage preferences
				
				Set<Integer> preferredMen = new HashSet(woman.getPreferences().keySet()); //set of who woman would marry
				

				
				for (int key : preferredMen) //loop through all her possible beloveds
				{
																	//get(key) > thisMan means get(key) is behind thisMan in the list of 1 ... max
					if (woman.getPreferences().get(key) > thisMan) //so this test determines if woman would rather marry "thisMan" than "key"
					{
						woman.remove(key);  //remove him from her list
						
						Person dude = findPerson(key, men); //now let's delete her from his list
						dude.remove(w);
					} 
				}
				
			}
			
		}
	}

	public static void readPreferences(File file, HashMap<Person,Integer> men,
			HashMap<Person,Integer> women) throws FileNotFoundException {
		/*
		 * Read in each man's preferences, when we see an alphabetical string,
		 * we create a new man with that as his name when we don't see an
		 * alphabetical string, we know that that is one of the man's numerical
		 * preferences and we add that preference to the last man added to the
		 * HashMap of men, we do this until we encounter "END", and we start
		 * over the same process, but for the women array list, until we
		 * encounter the second "END", in which case we stop reading from the
		 * file
		 */

		Scanner s = new Scanner(file);
		boolean readingInMen = true;

		String input = new String();
		int choiceNumber = 0; //keeps track of each match #'s  preference for each man, starting at 1
		int personNumber = -1; //counter for men
		Person person = new Person();
		
		
		while (s.hasNext()) {
			input = s.next();
			if (input.equals("END")) {
				if (readingInMen)
				{
					men.put(person, personNumber);
				}
				else
				{
					women.put(person, personNumber);
				}

				readingInMen = false;
				//now reset counters
				personNumber = -1;
				choiceNumber = 0;
				continue;
			}
			if (readingInMen) {
				

				if (Pattern.matches("[a-zA-Z]+.*?:+", input)) {
					
					if (personNumber != -1)  //have we already set up a man through input?  if yes, store him in men, then create a new person object
					{
							men.put(person, personNumber);
					}
					input = input.replace(':', ' ');	
					person = new Person(input);
					personNumber++;
					choiceNumber = 0; //new man so reset counter
				} else {
					choiceNumber++;
					int intInput = new Integer(input);
					person.addPreference(intInput, choiceNumber);
				}
			} else {
				if (Pattern.matches("[a-zA-Z]+.*?:+", input)) {
					
					if (personNumber != -1)  //have we already set up a woman through input?  if yes, store him in men, then re-assign person variable
					{
							women.put(person, personNumber);
					}
					input = input.replace(':', ' ');
					person = new Person(input);
					choiceNumber = 0;  //reset  counter
					personNumber++;
				} else {
					choiceNumber++;
					int intInput = new Integer(input);
					person.addPreference(intInput, choiceNumber);
				}
			}
		}

	}
