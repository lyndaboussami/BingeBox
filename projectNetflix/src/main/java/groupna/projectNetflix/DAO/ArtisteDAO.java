package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.utils.ConxDB;

public class ArtisteDAO {
    private static Connection conn = ConxDB.getInstance();
    public static int save(Artiste a) {
        String sql = "INSERT INTO artistes (fullname) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, a.getFullname());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenere = generatedKeys.getInt(1);
                        a.setId(idGenere); 
                        return idGenere;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ArtisteDAO (save) : " + e.getMessage());
        }
        return -1;
    }
    public static Artiste findById(int id) {
        String sql = "SELECT * FROM artistes WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Artiste(
                        rs.getInt("id"),
                        rs.getString("fullname")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ArtisteDAO (findById) : " + e.getMessage());
        }
        return null;
    }
    public static List<Artiste> findAll() {
        List<Artiste> artistes = new ArrayList<>();
        String sql = "SELECT * FROM artistes";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                artistes.add(new Artiste(
                    rs.getInt("id"),
                    rs.getString("fullname")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur ArtisteDAO (findAll) : " + e.getMessage());
        }
        return artistes;
    }
    public static int getIdIfExists(String fullname) {
        String sql = "SELECT id FROM artistes WHERE fullname = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullname);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ArtisteDAO (getIdIfExists) : " + e.getMessage());
        }
        return -1;
    }
}