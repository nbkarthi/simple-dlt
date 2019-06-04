package dlt.common;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
	//Applies Sha256 to a string and returns the result. 
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	public static <T> T fromJSON(final String json, final Class<T> clazz) {
		final Gson gson = (new GsonBuilder()).serializeSpecialFloatingPointValues().create();
		return gson.fromJson(json, clazz);
	}

	public static String toJSON(final Object obj) {
		final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
		return gsonBuilder.create().toJson(obj);
	}

	public static long now(){
		return Clock.systemUTC().millis();
	}
	
	public static String uid() {
		return Long.toString(Math.abs(UUID.randomUUID().getLeastSignificantBits()), Character.MAX_RADIX).toUpperCase();
	}
	

}