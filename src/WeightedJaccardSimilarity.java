/**
 * 
 */
package com.vars.videoadanalysis;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author VARS
	
	This code calculates wighted jaccard similarity between each video text and given dictionary
 *
 */
public class WeightedJaccardSimilarity {

	/**
	 * Dictionary global variables
	 */
	static HashMap<String, String> attackWordList;
	static HashMap<String, String> defendWordList;
	static ArrayList<String> republicanNameList;
	static ArrayList<String> democratNameList;

	static HashMap<String, String> videoDetails;

	static String otherCandidates;

	/**
	 * Default constructor
	 */
	public WeightedJaccardSimilarity() {
		attackWordList = new HashMap<String, String>();
		defendWordList = new HashMap<String, String>();
		republicanNameList = new ArrayList<String>();
		democratNameList = new ArrayList<String>();
		videoDetails = new HashMap<String, String>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {

		long startTime = System.nanoTime();
		
		FetchFromDB objFetchFromDB = new FetchFromDB("root", "abhinav0361",
				"jdbc:mysql://localhost/ad_analysis");

		String query = null, type = null, speaker;

		Double simIndex_attack = null, simIndex_defense = null;

		type = new String("word");

		// Fetch Attacking word list
		query = new String("SELECT word, weight FROM Attack;");
		attackWordList = objFetchFromDB.retrieve(query);

		// Fetch Defending word list
		query = new String("SELECT word, weight FROM Defend;");
		defendWordList = objFetchFromDB.retrieve(query);

		type = new String("Name");

		// Fetch Republican Name list
		query = new String("SELECT Name FROM Republic_Senator;");
		republicanNameList = objFetchFromDB.retrieve(query, type);

		// Fetch Democrat Name list
		query = new String("SELECT Name FROM Democrat_Senator;");
		democratNameList = objFetchFromDB.retrieve(query, type);

		// Fetch Video Text
		// query = new String("SELECT video FROM processed ORDER BY vid;");
		videoDetails = objFetchFromDB.retrieve();

		for (String key : videoDetails.keySet()) {

			String VideoText = videoDetails.get(key);

			// Convert to Array List
			ArrayList<String> videoTextToArrayList = new ArrayList<String>();
			for (String word : VideoText.split(" ")) {
				videoTextToArrayList.add(word);
			}

			simIndex_attack = similarity(videoTextToArrayList, attackWordList);
			simIndex_defense = similarity(videoTextToArrayList, defendWordList);

			speaker = findPersons(videoTextToArrayList, key);

			if (simIndex_attack < simIndex_defense) {
				if (speaker != null && !speaker.isEmpty()) {
					System.out.print(speaker + " In the Video: \"" + key
							+ "\" is Defending/Taking about self");

					if (otherCandidates != null && !otherCandidates.isEmpty()
							&& !otherCandidates.contains(speaker))
						System.out.print(" and mentioned about "
								+ otherCandidates);

				} else
					System.out.print("Video: \"" + key
							+ "\" is an Defending video with");
			} else {
				if (speaker != null && !speaker.isEmpty()) {
					System.out.print(speaker + " In the Video: \"" + key
							+ "\" is Attacking ");

					if (otherCandidates != null && !otherCandidates.isEmpty()
							&& !otherCandidates.contains(speaker))
						System.out.print(" and mentioned about "
								+ otherCandidates);

				} else
					System.out.print("Video: \"" + key
							+ "\" is an attacking video with");

			}
			System.out.println("\nsimIndex_attack: " + simIndex_attack);
			System.out.println("simIndex_defense: " + simIndex_defense + "\n");

		}
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 

	}

	private static String findPersons(ArrayList<String> videoTextToArrayList,
			String key) {

		String speaker = null;
		ArrayList<String> candidatesList = new ArrayList<String>();
		candidatesList.addAll(republicanNameList);
		candidatesList.addAll(democratNameList);

		FetchNames objFetchNames = new FetchNames(videoTextToArrayList,
				candidatesList);

		speaker = objFetchNames.speakerNew;
		otherCandidates = objFetchNames.otherCandidates;
		if (speaker.isEmpty()) {

			ListIterator<String> itrRep = republicanNameList.listIterator();
			while (itrRep.hasNext()) {
				speaker = itrRep.next();
				String temp[] = speaker.split(" ");
				for (int i = 0; i < temp.length; i++) {
					if (key.toLowerCase()
							.indexOf("by " + temp[i].toLowerCase()) != -1) {
						return speaker;
					}
				}

			}

			ListIterator<String> itrDem = democratNameList.listIterator();
			while (itrDem.hasNext()) {
				speaker = itrDem.next();
				String temp[] = speaker.split(" ");
				for (int i = 0; i < temp.length; i++) {
					if (key.toLowerCase()
							.indexOf("by " + temp[i].toLowerCase()) != -1) {
						return speaker;
					}
				}

			}
		}
		return objFetchNames.speakerNew;
	}

	/**
	 * @param videoTextToArrayList
	 *            ,dictionary
	 * @return modified jaccard similarity
	 */

	private static double similarity(ArrayList<String> videoTextToArrayList,
			HashMap<String, String> attackWordList) {

		int jaccardWeight = 0;
		String key = null;
		Set<String> unionXY = new HashSet<String>(attackWordList.keySet());
		unionXY.addAll(videoTextToArrayList);

		if (videoTextToArrayList.size() == 0 || attackWordList.size() == 0)
			return 0.0;

		ListIterator<String> listIter = videoTextToArrayList.listIterator();

		while (listIter.hasNext()) {
			key = listIter.next();
			if (attackWordList.containsKey(key))
				jaccardWeight += Integer.parseInt(attackWordList.get(key));
		}

		return (double) jaccardWeight / (double) unionXY.size();
	}

}
