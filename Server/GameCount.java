import java.util.*;
import java.io.*;
import data.structures.*;
import data.searching.*;

class GameCounter extends Thread{
	public long count;
	private fileDistributor fd;

	public GameCounter(fileDistributor fd){
		this.count = 0;
		this.fd = fd;
	}

	public void run(){
		try{
			String dataFile = fd.popFirstFile();
			while(dataFile!=null){
				FileInputStream in = new FileInputStream("Src/"+dataFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				System.out.println(this.getName()+" : Processing "+dataFile);
				do{
					reader.readLine();
					this.count++;
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

public class GameCount{
	public static void main(String[] args) {
		try{
			final int NB_THREAD;
			if(args.length>0)NB_THREAD = Integer.parseInt(args[0]);
			else NB_THREAD = 1;
			long overallCpt=0;
			File folder = new File("Src/");
			ArrayList<String> fileFolder = new ArrayList<>();
			for(int i=0;i<folder.list().length;i++){
				if(folder.list()[i].endsWith("_url_index.dat"))fileFolder.add(folder.list()[i]);
			}
			fileFolder.sort(String::compareToIgnoreCase);
			fileDistributor fd = new fileDistributor(fileFolder);
			ArrayList<GameCounter> cTab = new ArrayList<GameCounter>();
			for(int i=0;i<NB_THREAD;i++){
				cTab.add(new GameCounter(fd));
			}
			for(int i=0;i<NB_THREAD;i++){
				cTab.get(i).start();
			}
			for(int i=0;i<NB_THREAD;i++){
				cTab.get(i).join();
				overallCpt+=cTab.get(i).count;
			}
			System.out.format("%,8d games in total\n", overallCpt);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
