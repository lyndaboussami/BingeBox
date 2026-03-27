package groupna.projectNetflix.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.entities.Visualisable;
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
                    Set<Oeuvre> favs = getAllUserFavorites(id);
                    Map<LocalDate, List<Visualisable>> his =getHistoryGroupedByDate(id);

                    user = new User(id, nom, prenom, email, mdp, role, favs, his);
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
        String sqlFilms = "SELECT id_oeuvre, date_visionnage FROM historique_film WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlFilms)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Oeuvre f = FilmDAO.findById(rs.getInt("id_oeuvre"));
                    if (f != null) {
                        globalHistory.add(new HistoryItem(f, rs.getTimestamp("date_visionnage")));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        String sqlEpisodes = "SELECT id_episode, date_visionnage FROM historique_episodes WHERE id_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlEpisodes)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Episode ep = EpisodeDAO.getEpisodeById(rs.getInt("id_episode"));
                    if (ep!=null) {
                        globalHistory.add(new HistoryItem(ep, rs.getTimestamp("date_visionnage")));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return globalHistory.stream()
                .sorted((h1, h2) -> h2.getDateVisionnage().compareTo(h1.getDateVisionnage()))
                .collect(Collectors.toList());
    }
//-------------------------------------------------------------------------------------------------
    public static Map<LocalDate, List<Visualisable>> getHistoryGroupedByDate(int idUser) {
        List<HistoryItem> flatHistory = getUserFullGlobalHistory(idUser);
        return flatHistory.stream()
            .collect(Collectors.groupingBy(
                item -> item.getDateVisionnage().toLocalDateTime().toLocalDate(),
                TreeMap::new,
                Collectors.mapping(
                    item -> (Visualisable) item.getContent(),
                    Collectors.toList()
                )
            )).descendingMap();
    }
//-------------------------------------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------------
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
//----------------------------------------------------------------------------------------
    public static void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        addToCollection(idUser, idOeuvre, tableName);
        System.out.println("[INFO] Ajouté aux favoris (" + tableName + ")");
    }
//----------------------------------------------------------------------------------------
    public static void ajouterAHistoriqueFilm(int idUser, int idFilm) {
        String tableName = "historique_film";
        addToCollection(idUser, idFilm, tableName);
        
        System.out.println("[INFO] Film ajouté à l'historique (ID Film: " + idFilm + ")");
    }
//----------------------------------------------------------------------------------
    public static void ajouterAHistoriqueEpisode(int idUser, int idEpisode) {
        String sql = "INSERT INTO historique_episodes (id_user, id_episode, date_visionnage) VALUES (?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idEpisode);

            pstmt.executeUpdate();
            System.out.println("[SQL] Nouveau visionnage enregistré pour l'épisode " + idEpisode);
            
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Impossible d'ajouter le visionnage à l'historique.");
            e.printStackTrace();
        }
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

}