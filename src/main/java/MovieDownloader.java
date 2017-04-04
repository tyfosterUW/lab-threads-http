import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	/**
	* Primary method that returns an array of Strings containing movie data
	*/
	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API, catches strange characters
		String urlString = "";
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String[] movies = null; //Empty string array to be returned

		try {

			URL url = new URL(urlString); //Constructs an URL out of urlString

			urlConnection = (HttpURLConnection) url.openConnection(); //Opens Http connection
			urlConnection.setRequestMethod("GET"); //Requesting data, not posting
			urlConnection.connect();	//Attempt to connect

			InputStream inputStream = urlConnection.getInputStream(); 
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = reader.readLine(); //Read each line of received data
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			//Return null if no results
			if (buffer.length() == 0) {
				return null;
			}
			String results = buffer.toString(); //Transfer result data into result array
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n"); //Construct an array based on new lines in the results string
		} 
		catch (IOException e) { //Catch errors regarding input/output 
			return null;
		} 
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect(); //Disconnect on finish
			}
			if (reader != null) {
				try {
					reader.close(); //Close reader on finish
				} 
				catch (IOException e) {
				}
			}
		}

		//Return movies array
		return movies;
	}


	//What happens when the program is ran
	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in); //Allows for user input

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){ //Close command
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm); //If not close command, send a reqeust based on user input
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
