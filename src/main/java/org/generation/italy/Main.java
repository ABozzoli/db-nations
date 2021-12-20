package org.generation.italy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		String url = "jdbc:mysql://localhost:3306/nations";
		String user = "root";
		String password = "rootpassword";

		Scanner scan = new Scanner(System.in);
		
		try (Connection con = DriverManager.getConnection(url, user, password)) {

			// ricerca che contenga
			System.out.print("Search: ");
	        String search = scan.nextLine();
			
	        System.out.format("%3s%45s%33s%18s\n", "ID", "COUNTRY", "REGION", "CONTINENT");
	        
			String query = "SELECT n.country_id, n.name, r.name, c.name\r\n"
					+ "FROM countries n \r\n"
					+ "INNER JOIN regions r \r\n"
					+ "ON n.region_id = r.region_id\r\n"
					+ "INNER JOIN continents c \r\n"
					+ "ON r.continent_id = c.continent_id\r\n"
					+ "WHERE n.name LIKE CONCAT('%', ?, '%')\r\n"
					+ "ORDER BY n.name;";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				
				ps.setString(1, search);
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						int countryId = rs.getInt(1);
						String countryName = rs.getString(2);
						String regionName = rs.getString(3);
						String continentName = rs.getString(4);
						System.out.format("%3d%45s%33s%18s\n", countryId, countryName, regionName, continentName);
					}
				}
				
			}
			
			// ricerca tramite id
			System.out.print("Choose a country id: ");
	        int searchId = scan.nextInt();
	        
	        System.out.print("Languages: ");
	        
	        query = "SELECT `language`\r\n"
	        		+ "FROM country_languages ponte \r\n"
	        		+ "INNER JOIN languages l \r\n"
	        		+ "ON ponte.language_id = l.language_id\r\n"
	        		+ "INNER JOIN countries c \r\n"
	        		+ "ON ponte.country_id = c.country_id\r\n"
	        		+ "WHERE ponte.country_id = ?;";
	        
			try (PreparedStatement ps = con.prepareStatement(query)) {
				
				ps.setInt(1, searchId);
				
				try (ResultSet rs = ps.executeQuery()) {
					String languages = "";
					while (rs.next()) {
						languages += rs.getString(1);
						if (!rs.isLast()) {
							languages += ", ";
						}
					}
					System.out.println(languages);
				}
				
			}
			
			// statistiche più recenti
			System.out.println("Most recent stats");
			
			query = "SELECT `year`, population, gdp\r\n"
					+ "FROM country_stats cs \r\n"
					+ "WHERE country_id = ?\r\n"
					+ "ORDER BY `year` DESC\r\n"
					+ "LIMIT 1;";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				
				ps.setInt(1, searchId);
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						System.out.println("Year: " + rs.getInt(1));
						System.out.println("Population: " + rs.getInt(2));
						System.out.println("GDP: " + rs.getBigDecimal(3));
					}
				}
				
			}

		} catch (SQLException e) {
			System.out.println("OOOPS, si è verificato un errore:");
			System.out.println(e.getMessage());
		}
		
		scan.close();

	}

}
