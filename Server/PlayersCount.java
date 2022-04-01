import java.util.*;
import java.io.*;
import data.structures.*;
import data.searching.*;

class Counter extends Thread{
	private fileDistributor fd;
	private Hashtable<String,Boolean> counts;

	public Counter(fileDistributor fd, Hashtable<String,Boolean> counts){
		this.fd = fd;
		this.counts = counts;
	}

	public int getCount(){
		return counts.size();
	}

	public void run(){
		try{
			String dataFile = fd.popFirstFile();
			while(dataFile!=null){
				FileInputStream in = new FileInputStream("Src/"+dataFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				System.out.println(this.getName()+" : Processing "+dataFile);
				do{
					String line = reader.readLine();
					if(line.startsWith("[Pseudo ")){
						synchronized(counts){
							if(!counts.containsKey(line.substring(8,line.length()-2))){
								this.counts.put(line.substring(8,line.length()-2),true);
							}
						}
					}
				}while(reader.ready());
				reader.close();
				in.close();
				dataFile = fd.popFirstFile();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}

public class PlayersCount{
	public static void main(String[] args) {
		try{
			final int NB_THREAD;
			if(args.length>0)NB_THREAD = Integer.parseInt(args[0]);
			else NB_THREAD = 1;
			long overallCpt=0;
			File folder = new File("Src/");
			ArrayList<String> fileFolder = new ArrayList<String>();
			for(int i=0;i<folder.list().length;i++){
				if(folder.list()[i].endsWith("_player_data.dat"))fileFolder.add(folder.list()[i]);
			}
			fileFolder.sort(String::compareToIgnoreCase);
			fileDistributor fd = new fileDistributor(fileFolder);
			ArrayList<Counter> cTab = new ArrayList<Counter>();
			Hashtable<String,Boolean> counts = new Hashtable<String,Boolean>();
			for(int i=0;i<NB_THREAD;i++){
				cTab.add(new Counter(fd,counts));
			}
			for(int i=0;i<NB_THREAD;i++){
				cTab.get(i).start();
			}
			for(int i=0;i<NB_THREAD;i++){
				cTab.get(i).join();
				overallCpt=cTab.get(i).getCount();
			}
			System.out.format("%,8d players in total\n", overallCpt);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
