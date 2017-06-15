package com.example.robert.opennlptest;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import opennlp.tools.parser.Parse;

/**
 * Created by robert on 18/05/17.
 * processes voice queries
 */

public class VoiceQuery {
	
	private List<Parse> questions = new ArrayList<Parse>();//a list of identified question clauses
	private List<Parse> wh = new ArrayList<Parse>();//list of whadverbs
	private List<Parse> verbphrases = new ArrayList<Parse>();//list of verb phrases
	private List<Parse> nounphrases = new ArrayList<Parse>();//list of noun phrases
	private Parse primaryNP = null;//the primary noun phrase
	private boolean error = false;

	public VoiceQuery(Parse p)
	{
		TreeTraverse(p);
	}

	public VoiceQuery(List<Parse> questions,List<Parse> wh, List<Parse> verbphrases,List<Parse> nounphrases, Parse primaryNP)
	{
		this.questions = questions;
		this.wh = wh;
		this.verbphrases = verbphrases;
		this.nounphrases = nounphrases;
		this.primaryNP = primaryNP;
	}
	//Getters
	public List<Parse> GetQuestions()
	{
		return this.questions;
	}
	public List<Parse> GetWH()
	{
		return this.wh;
	}
	public List<Parse> GetVerbPhrases()
	{
		return this.verbphrases;
	}
	public List<Parse> GetNounPhrases()
	{
		return this.nounphrases;
	}
	public Parse GetPrimaryNP() {return this.primaryNP; }
	public boolean IsError() { return this.error; }

	/**
	 * traverses a question parse tree and extracts information
	 * @param p the parse tree of a question
	 */
	public void TreeTraverse(Parse p)
	{
		try {

			questions = new ArrayList<Parse>();
			wh = new ArrayList<Parse>();
			verbphrases = new ArrayList<Parse>();
			nounphrases = new ArrayList<Parse>();
			primaryNP = null;

			Queue q = new LinkedList();
			q.add(p);
			//Breadth first traverse looking for basic chunks
			while (!q.isEmpty()) {
				Parse parse = (Parse) q.remove();
				for (int i = 0; i < parse.getChildCount(); i++) {
					Parse child = parse.getChildren()[i];
					System.out.println(child.toString() + " " + child.getType());
					if (child.getType().equals("SQ") || child.getType().equals("SBARQ") || child.getType().equals("SBAR")) {
						questions.add(child);
					}
					if (child.getType().equals("WRB") || child.getType().equals("WP")) {
						wh.add(child);
					}
					if (child.getParent().getType().equals("VP") && (child.getType().equals("VB") || child.getType().equals("VBD") || child.getType().equals("VBN"))) {
						verbphrases.add(child);
					}
					if (child.getType().equals("NP")) {
						boolean rootNP = true;
						for (int j = 0; j < child.getChildCount(); j++) {
							if (child.getChildren()[j].getType().equals("NP")) {
								rootNP = false;
							}
						}
						if (rootNP) {
							System.out.println("Adding np: " + child.toString());
							nounphrases.add(child);
						}

					}
					q.add(child);
				}
				System.out.println();
			}
			//Traverse final question chunk looking for most significant noun phrase
			Queue questionq = new LinkedList();
			if (questions.size() > 0) {
				questionq.add(questions.get(0));
			} else {
				questionq.add(p);
			}

			List<Parse> np = new ArrayList<Parse>();

			while (!questionq.isEmpty()) {
				Parse parse = (Parse) questionq.remove();
				for (int i = 0; i < parse.getChildCount(); i++) {
					Parse child = parse.getChildren()[i];
					System.out.println("questions: " + child.toString() + " " + child.getType());
					if (child.getType().equals("NP")) {
						boolean rootNP = true;
						for (int j = 0; j < child.getChildCount(); j++) {
							if (child.getChildren()[j].getType().equals("NP")) {
								rootNP = false;
							}
						}
						if (rootNP) {
							System.out.println("Adding np: " + child.toString());
							np.add(child);
						}

					}
					questionq.add(child);
				}
			}
			//resolves pronouns
			if (!np.isEmpty()) {
				if (np.get(0).getChildren()[0].getType().equals("PRP") || np.get(0).getChildren()[0].getType().equals("PRP$")) {
					for(int i=0;i<nounphrases.size();i++)
					{
						//ignores existential
						if(!nounphrases.get(i).getChildren()[0].getType().equals("EX") && nounphrases.get(i).getChildCount() <= 1)
					    {
							primaryNP = nounphrases.get(i);
                            nounphrases.removeAll(Collections.singleton(primaryNP));
							break;
						}
					}

				} else {
					primaryNP = np.get(0);
					nounphrases.removeAll(Collections.singleton(primaryNP));
				}
			}
			//removes less useful noun phrases
            for(int i = 0; i<nounphrases.size();i++)
            {
                if((nounphrases.get(i).getChildren()[0].getType().equals("DT") || nounphrases.get(i).getChildren()[0].getType().equals("EX") || nounphrases.get(i).getChildren()[0].getType().equals("PRP") || nounphrases.get(i).getChildren()[0].getType().equals("PRP$")) && nounphrases.get(i).getChildCount() <= 1)
                {
                    nounphrases.remove(i);
                    i--;
                }
                else if (nounphrases.get(i).getChildren()[0].getType().equals("DT") || nounphrases.get(i).getChildren()[0].getType().equals("PRP$"))
                {
                    nounphrases.get(i).remove(0);
                }

            }


			for (int i = 0; i < questions.size(); i++) {
				System.out.println(questions.get(i));
			}
			for (int i = 0; i < wh.size(); i++) {
				System.out.println(wh.get(i));
			}
			for (int i = 0; i < verbphrases.size(); i++) {
				System.out.println(verbphrases.get(i));
			}
			for (int i = 0; i < nounphrases.size(); i++) {
				System.out.println(nounphrases.get(i));
			}
			//strips primary noun phrases leading determiners
			if (primaryNP != null && primaryNP.getChildren()[0].getType().equals("DT")) {
				primaryNP.remove(0);
			}
			System.out.println("primary term: "+this.primaryNP.toString());
		}
		catch (Exception e)
		{
			Log.d("parsing", "TreeTraverse: " + e.getMessage());
			error = true;
		}
	}

	/**
	 * generates a search term from the question data
	 * @return search term
	 */
	public String GenerateTerm()
    {
        String term = "";
        if(!error)
        {
            term = this.GetPrimaryNP().toString();
            for(int i = 0; i < this.nounphrases.size(); i++)
            {
                term += " "+this.nounphrases.get(i).toString();
            }
        }
        else
        {
            term = "error"; //This should never really be relied upon, errors should be handled at search time
        }
        Log.d("term", "GenerateTerm: " + term);
        return term;
    }

}
