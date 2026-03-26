package groupna.projectNetflix.DAO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.utils.ConxDB;

public class UserDAO {
    private static Connection conn = ConxDB.getInstance();
    public static User login(String email, String mdp) {
        User user = null;
        String sql = "SELECT * FROM user WHERE email = ? AND mdp = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, mdp);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    user = findById(id); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User findById(int id) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String mdp = rs.getString("mdp");
                    Role role = Role.valueOf(rs.getString("role"));
                    Set<Oeuvre> favs = getAllUserFavorites(id);
                    Set<Oeuvre> his =getUserFullHistory(id);

                    user = new User(id, nom, prenom, email, mdp, role, favs, his);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static int save(User u) {
        int generatedId = 0;
        String sql = "INSERT INTO user (nom, prenom, email, mdp, role) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, u.getNom());
            pstmt.setString(2, u.getPrenom());
            pstmt.setString(3, u.getEmail());
            pstmt.setString(4, u.getMdp());
            pstmt.setString(5, u.getRole().name());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    u.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }
    public static void addToCollection(int idUser, int idOeuvre, String tableName) {
        String sql = "INSERT IGNORE INTO " + tableName + " (id_user, id_oeuvre) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Set<Oeuvre> getUserFullHistory(int idUser) {
        Set<Oeuvre> historiqueGlobal = new HashSet<>();
        
        // Simple query since both tables now provide the ID we need directly
        String sql = "SELECT id_item, type_item FROM (" +
                     "SELECT id_oeuvre AS id_item, 'FILM' AS type_item FROM historique_film WHERE id_user = ? " +
                     "UNION " +
                     "SELECT DISTINCT id_oeuvre AS id_item, 'SERIE' AS type_item FROM historique_series WHERE id_user = ?" +
                     ") AS global_hist";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idUser);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_item");
                    String type = rs.getString("type_item");

                    if ("FILM".equals(type)) {
                        Oeuvre f = FilmDAO.findById(id);
                        if (f != null) historiqueGlobal.add(f);
                    } else {
                        Oeuvre s = SerieDAO.findById(id);
                        if (s != null) historiqueGlobal.add(s);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historiqueGlobal;
    }
    public static void removeFromCollection(int idUser, int idOeuvre, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE id_user = ? AND id_oeuvre = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("L'élément a été retiré de la table " + tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Set<Oeuvre> getAllUserFavorites(int idUser) {
        Set<Oeuvre> oeuvres = new HashSet<>();
        String sql = "SELECT id_oeuvre FROM fav_film WHERE id_user = ? " +
                     "UNION " +
                     "SELECT id_oeuvre FROM fav_serie WHERE id_user = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idUser);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idO = rs.getInt("id_oeuvre");
                    Oeuvre o = FilmDAO.findById(idO);
                    if (o == null) {
                        o = SerieDAO.findById(idO);
                    }

                    if (o != null) {
                        oeuvres.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des favoris : " + e.getMessage());
        }
        return oeuvres;
    }
    public static void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        addToCollection(idUser, idOeuvre, tableName);
        System.out.println("[INFO] Ajouté aux favoris (" + tableName + ")");
    }
    public static void ajouterAHistorique(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "historique_film" : "historique_series";
        addToCollection(idUser, idOeuvre, tableName);
        System.out.println("[INFO] Ajouté à l'historique (" + tableName + ")");
    }
}