/**
 * 
 */
package com.vars.videoadanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author VARS
 
 This code determines normal jaccardSimilarity between a video text and given dictionary
 *
 */
public class JaccardSimilarity {

	/**
	 * Dictionary global variables
	 */
	static ArrayList<String> attackWordList;
	static ArrayList<String> defendWordList;
	static ArrayList<String> republicanNameList;
	static ArrayList<String> democratNameList;

	static HashMap<String, String> videoDetails;

	static String otherCandidates;

	/**
	 * Default constructor
	 */
	protected JaccardSimilarity() {
		attackWordList = new ArrayList<String>();
		defendWordList = new ArrayList<String>();
		republicanNameList = new ArrayList<String>();
		democratNameList = new ArrayList<String>();
		videoDetails = new HashMap<String, String>();
		otherCandidates = null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();

		FetchFromDB objFetchFromDB = new FetchFromDB("root", "abhinav0361",
				"jdbc:mysql://localhost/ad_analysis");

		String query = null, type = null, speaker;

		Double simIndex_attack = null, simIndex_defense = null;

		type = new String("word");

		// Fetch Attacking word list
		query = new String("SELECT word FROM Attack;");
		attackWordList = objFetchFromDB.retrieve(query, type);

		// Fetch Defending word list
		query = new String("SELECT word FROM Defend;");
		defendWordList = objFetchFromDB.retrieve(query, type);

		type = new String("Name");

		// Fetch Republican Name list
		query = new String("SELECT Name FROM Republicans;");
		republicanNameList = objFetchFromDB.retrieve(query, type);

		// Fetch Democrat Name list
		query = new String("SELECT Name FROM Democrates;");
		democratNameList = objFetchFromDB.retrieve(query, type);

		// Fetch Video Text
		// query = new String("SELECT video FROM processed ORDER BY vid;");
		videoDetails = objFetchFromDB.retrieve();

		for (String key : videoDetails.keySet()) {
			otherCandidates = null;
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
		System.out.println("Took " + (endTime - startTime) + " ns");
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
	 * @return modified jaccardSimilarity
	 */

	protected static double similarity(ArrayList<String> videoTextToArrayList,
			ArrayList<String> dictionary) {

		if (videoTextToArrayList.size() == 0 || dictionary.size() == 0) {
			return 0.0;
		}

		Set<String> unionXY = new HashSet<String>(videoTextToArrayList);
		unionXY.addAll(dictionary);

		Set<String> intersectionXY = new HashSet<String>(videoTextToArrayList);
		intersectionXY.retainAll(dictionary);

		return (double) intersectionXY.size() / (double) unionXY.size();
	}

}
