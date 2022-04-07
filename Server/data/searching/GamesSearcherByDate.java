package data.searching;

import java.io.*;
import java.util.*;
import data.structures.*;

public class GamesSearcherByDate{
	public String date;
	public String month;
	public String year;
	public String day;

	public GamesSearcherByDate(String date){
	//format YYYY.MM.DD
		this.date = date;
		this.month = date.substring(5,date.length()-3);
		this.year = date.substring(0,date.length()-6);
		this.day = date.substring(8,date.length());
	}

	public boolean doesExists() {
		System.out.println("currently testin...");
		File folder = new File("Src/");
		ArrayList<String> fileFolder = new ArrayList<>();
		for(int i=0;i<folder.list().length;i++){
			if(folder.list()[i].endsWith(".pgn")){
				fileFolder.add(folder.list()[i]);
			}
		}
		fileFolder.sort(String::compareToIgnoreCase);
		String toSearch = "lichess_db_standard_rated_"+this.year+"-"+this.month+".pgn";
		System.out.println(toSearch);
		Boolean res = false;
		for(int i=0; i<fileFolder.size() && !res;i++){
			if(fileFolder.get(i).equals(toSearch)) res=true;
		}
		return res;
	}


	public Game[] dateLoad(int nbGame){
		String file = "lichess_db_standard_rated_"+this.year+"-"+this.month+".pgn";
		int cptGame=0;
	//System.out.println(file);
		ArrayList<Game> gameArray = new ArrayList<Game>();
		try{
			FileInputStream in = new FileInputStream("Src/"+file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			do{
				System.out.println("je recherche une partie");
				Game tmp;
				int blankLineCpt = 0;
				String lineContent="";

				if(Integer.parseInt(this.day)>1){
					System.out.println("je suis dans le if");
					int dayLower = Integer.parseInt(this.day)-1;
					String dayLowerString="";
					if(dayLower<10){
						dayLowerString = "0"+dayLower;
					}else{
						dayLowerString = dayLower+"";
					}
					String modifiedDate = this.year+"."+this.month+"."+dayLowerString;
					String lineToCompare="[UTCDate \""+modifiedDate+"\"]";
					Boolean quit = false;
					do{
						lineContent = reader.readLine();
					}while(!lineContent.equals(lineToCompare));
					do{
						if(reader.readLine().equals("")) quit=true;
					}while(!quit);
					reader.readLine();
			//		System.out.println("je suis dehors le if");
					this.day=0+"";
					System.out.println("end of if");
				}
				tmp = new Game();
				do{

					lineContent = reader.readLine();

		        //System.out.println(lineContent);
					if(lineContent != null){
						if(lineContent.startsWith("[Event")){
							tmp.type = lineContent.substring(8,lineContent.length()-2);
						}
						if(lineContent.startsWith("[Site")){
							tmp.url = lineContent.substring(27,lineContent.length()-2);
						}
						if(lineContent.startsWith("[White ")){
							tmp.whitePlayer = lineContent.substring(8,lineContent.length()-2);
						}
						if(lineContent.startsWith("[Black ")){
							tmp.blackPlayer = lineContent.substring(8,lineContent.length()-2);
						}
						if(lineContent.startsWith("[Result")){
							tmp.result = lineContent.substring(9,lineContent.length()-2);
						}
						if(lineContent.startsWith("[UTCDate ")){
							tmp.date = lineContent.substring(10,lineContent.length()-2);
						}
						if(lineContent.startsWith("[Opening")){
							tmp.opening = lineContent.substring(10,lineContent.length()-2);
						}
						if(lineContent.equals("")){
							blankLineCpt++;
						}
					}

					if(blankLineCpt>1){
						if(tmp.date.equals(this.date)){
							gameArray.add(tmp);
							//System.out.println(tmp.toString());
							cptGame++;
						}
					}
				}while(blankLineCpt < 2);
				gameArray.add(tmp);
		      	System.out.println(tmp.date);

			}while(reader.ready() && cptGame<nbGame);
			for(int i=0;i<gameArray.size();i++){
				System.out.println(gameArray.get(i).toString());
			}



    // for(int i=0;i<games.length && cptGame<nbGame;i++){
    // 	 if(!(games[i].date.equals(this.date))){
    //   	games[i]=null;
    //   }else{
    //   	cptGame++;
    //   }
    // }
    // System.out.println("fin");


		}catch(IOException e){
			e.printStackTrace();
		}
		Game[] res = new Game[gameArray.size()];
		gameArray.toArray(res);
		return res;
	}
}