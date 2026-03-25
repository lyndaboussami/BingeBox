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
        String sql = "INSERT INTO artistes (nom, prenom, bio) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, a.getNom());
            pstmt.setString(2, a.getPrenom());
            pstmt.setString(3, a.getBio());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenere = generatedKeys.getInt(1);
                        a.setId(idGenere); 
                        if (a.getOeuvreMajeurs() != null && !a.getOeuvreMajeurs().isEmpty()) {
                            saveOeuvresMajeures(a);
                        }
                        return idGenere;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ArtisteDAO : " + e.getMessage());
        }
        return -1;
    }
    private static void saveOeuvresMajeures(Artiste a) {
        String sql = "INSERT IGNORE INTO artiste_oeuvres_majeures (id_artiste, nom_oeuvre) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String oeuvre : a.getOeuvreMajeurs()) {
                pstmt.setInt(1, a.getId());
                pstmt.setString(2, oeuvre);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion des oeuvres majeures : " + e.getMessage());
        }
    }
    public static Artiste findById(int id) {
        String sql = "SELECT * FROM artistes WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    List<String> oeuvres = findOeuvresByArtiste(id);
                    Artiste a = new Artiste(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("bio"),
                        oeuvres
                    );
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> findOeuvresByArtiste(int idArtiste) {
        List<String> liste = new ArrayList<>();
        String sql = "SELECT nom_oeuvre FROM artiste_oeuvres_majeures WHERE id_artiste = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idArtiste);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(rs.getString("nom_oeuvre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    public static List<Artiste> findAll() {
        List<Artiste> artistes = new ArrayList<>();
        String sql = "SELECT * FROM artistes";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String bio = rs.getString("bio");
                List<String> oeuvres = findOeuvresByArtiste(id);
                Artiste a = new Artiste(id, nom, prenom, bio, oeuvres);
                
                artistes.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des artistes : " + e.getMessage());
            e.printStackTrace();
        }
        return artistes;
    }
    public static int getIdIfExists(String nom, String prenom) {
        String sql = "SELECT id FROM artistes WHERE nom = ? AND prenom = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'artiste : " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}