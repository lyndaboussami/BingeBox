package groupna.projectNetflix.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.utils.ConxDB;

public class UserDAO {
    private static Connection conn = ConxDB.getInstance();    

//--------------------------------------------------------------------------------------
    
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
        if(user!=null && !user.getRole().equals(Role.ADMIN)) {
        	incrementerLoginDuJour();
        }
        return user;
    }
//-----------------------------------------------------------------------------------------
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

                    user = new User(id, nom, prenom, email, mdp, role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
//------------------------------------------------------------------------------------------------
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
        if(u.getRole().equals(Role.USER)) {
        	incrementerLoginDuJour();
        }
        return generatedId;
    }
//-----------------------------------------------------------------------------------------------------
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
//---------------------------------------------------------------------------
    public static List<HistoryItem> getUserFullGlobalHistory(int idUser) {
        List<HistoryItem> globalHistory = new ArrayList<>(); 
        String sqlFilms = "SELECT id_oeuvre, date_visionnage, time FROM historique_film WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFilms)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Film f = FilmDAO.findById(rs.getInt("id_oeuvre")); 
                    if (f != null) {
                        globalHistory.add(new HistoryItem(f, rs.getTimestamp("date_visionnage"), rs.getDouble("time")));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        String sqlEpisodes = "SELECT id_episode, date_visionnage, time FROM historique_episodes WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlEpisodes)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Episode ep = EpisodeDAO.getEpisodeById(rs.getInt("id_episode"));
                    if (ep != null) {
                        globalHistory.add(new HistoryItem(ep, rs.getTimestamp("date_visionnage"), rs.getDouble("time")));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return globalHistory.stream()
                .sorted((h1, h2) -> h2.getDateVisionnage().compareTo(h1.getDateVisionnage()))
                .collect(Collectors.toList());
    }
//-------------------------------------------------------------------------------------------------
    public static void removeFromCollection(int idUser, int idOeuvre, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE id_user = ? AND id_oeuvre = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            
            int rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//--------------------------------------------------------------------------------------------
    public static List<Oeuvre> getAllUserFavorites(int idUser) {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sqlFilms = "SELECT id_oeuvre FROM fav_film WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFilms)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Oeuvre f = FilmDAO.findById(rs.getInt("id_oeuvre"));
                    if (f != null) oeuvres.add(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur favoris films : " + e.getMessage());
        }
        String sqlSeries = "SELECT id_oeuvre FROM fav_serie WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSeries)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Oeuvre s = SerieDAO.findById(rs.getInt("id_oeuvre"));
                    if (s != null) oeuvres.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur favoris séries : " + e.getMessage());
        }

        return oeuvres;
    }
//----------------------------------------------------------------------------------------
    public static void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        addToCollection(idUser, idOeuvre, tableName);
    }
//----------------------------------------------------------------------------------------
    public static void ajouterAHistoriqueFilm(int idUser, int idFilm, double time) {
        String sql = "INSERT INTO historique_film (id_user, id_oeuvre, time) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idFilm);
            pstmt.setDouble(3, time);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] " + e.getMessage());
        }
    }
//----------------------------------------------------------------------------------
    public static void ajouterAHistoriqueEpisode(int idUser, int idEpisode, double time) {
        String sql = "INSERT INTO historique_episodes (id_user, id_episode, time) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idEpisode);
            pstmt.setDouble(3, time);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Impossible d'ajouter le visionnage à l'historique.");
            e.printStackTrace();
        }
    }
//-------------------------------------------------------------------------------------------------------
    public static void clearHistory(int idUser) {
        String sqlFilms = "DELETE FROM historique_film WHERE id_user = ?";
        String sqlEpisodes = "DELETE FROM historique_episodes WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFilms)) {
            pstmt.setInt(1, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Erreur] Échec de la suppression de l'historique films.");
            e.printStackTrace();
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sqlEpisodes)) {
            pstmt.setInt(1, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Erreur] Échec de la suppression de l'historique épisodes.");
            e.printStackTrace();
        }
    }
//----------------------------------------------------------------------------------------------------
    public static void update(User u) {
        String sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, mdp = ?, role = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getNom());
            pstmt.setString(2, u.getPrenom());
            pstmt.setString(3, u.getEmail());
            pstmt.setString(4, u.getMdp());
            pstmt.setString(5, u.getRole().name());
            pstmt.setInt(6, u.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user=findById(rs.getInt("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("[Erreur] Impossible de récupérer la liste des utilisateurs.");
            e.printStackTrace();
        }
        
        return users;
    }
//----------------------------------------------------------------------------------
    public static int getIdParEmail(String email) {
        int id = 0;
        String sql = "SELECT id FROM user WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'ID par email : " + e.getMessage());
            e.printStackTrace();
        }
        return id;
    }
//----------------------------------------------------------------------------------
    public static Map<LocalDate, Integer> getInscriptionsParDate() {
        Map<LocalDate, Integer> stats = new TreeMap<>();
        String sql = "SELECT date_inscris, COUNT(*) as nb_users " +
                     "FROM user " +
                     "WHERE date_inscris IS NOT NULL " +
                     "GROUP BY date_inscris " +
                     "ORDER BY date_inscris DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("date_inscris");
                if (sqlDate != null) {
                    LocalDate date = sqlDate.toLocalDate();
                    int count = rs.getInt("nb_users");
                    stats.put(date, count);
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Impossible de récupérer les statistiques d'inscription.");
            e.printStackTrace();
        }

        return stats;
    }
//-------------------------------------------------------------------------------------------------------
    public static void incrementerLoginDuJour() {
        String sql = "INSERT INTO LoginPerDay (date, nbLogins) " +
                     "VALUES (CURRENT_DATE, 1) " +
                     "ON DUPLICATE KEY UPDATE nbLogins = nbLogins + 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation du login : " + e.getMessage());
        }
    }
}