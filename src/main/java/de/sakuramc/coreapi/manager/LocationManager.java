package de.sakuramc.coreapi.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LocationManager {

    private final CoreAPI instance;
    private final File locationFile;
    private final Gson gson;
    private final Map<String, Location> locations;

    /**
     * Konstruktor für den LocationManager.
     *
     * @param instance Die Hauptklasse deines Plugins.
     */
    @SuppressWarnings({"CallToPrintStackTrace", "ResultOfMethodCallIgnored"})
    public LocationManager(final CoreAPI instance) {
        this.instance = instance;
        this.locationFile = new File(this.instance.getDataFolder(), "location/locations.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.locations = new HashMap<>();

        if (!locationFile.exists()) {
            try {
                this.instance.getDataFolder().mkdirs();
                locationFile.createNewFile();
                saveLocations();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadLocations();
    }

    /**
     * Lädt die Locations aus der JSON-Datei.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private void loadLocations() {
        try (Reader reader = new FileReader(locationFile)) {
            Type type = new TypeToken<Map<String, LocationSerializable>>() {}.getType();
            Map<String, LocationSerializable> serializedLocations = gson.fromJson(reader, type);
            if (serializedLocations != null) {
                for (Map.Entry<String, LocationSerializable> entry : serializedLocations.entrySet()) {
                    locations.put(entry.getKey(), entry.getValue().toLocation());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Speichert die aktuellen Locations in der JSON-Datei.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public void saveLocations() {
        try (Writer writer = new FileWriter(locationFile)) {
            Map<String, LocationSerializable> serializedLocations = new HashMap<>();
            for (Map.Entry<String, Location> entry : locations.entrySet()) {
                serializedLocations.put(entry.getKey(), new LocationSerializable(entry.getValue()));
            }
            gson.toJson(serializedLocations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fügt eine neue Location hinzu oder aktualisiert eine bestehende.
     *
     * @param name     Der Name der Location.
     * @param location Die Location-Objekt.
     */
    public void setLocation(String name, Location location) {
        locations.put(name.toLowerCase(), location);
        saveLocations();
    }

    /**
     * Holt eine Location anhand ihres Namens.
     *
     * @param name Der Name der Location.
     * @return Die Location oder null, wenn sie nicht gefunden wurde.
     */
    public Location getLocation(String name) {
        return locations.get(name.toLowerCase());
    }

    /**
     * Entfernt eine Location anhand ihres Namens.
     *
     * @param name Der Name der Location.
     */
    public void removeLocation(String name) {
        locations.remove(name.toLowerCase());
        saveLocations();
    }

    /**
     * Holt alle gespeicherten Locations.
     *
     * @return Eine Map von Namen zu Location-Objekten.
     */
    public Map<String, Location> getAllLocations() {
        return new HashMap<>(locations);
    }

    /**
     * Eine innere Klasse zur Serialisierung von Location-Objekten.
     */
    private static class LocationSerializable {
        private double x, y, z;
        private float yaw, pitch;
        private String world;

        public LocationSerializable(Location location) {
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
            this.world = location.getWorld().getName();
        }

        public Location toLocation() {
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
    }

}
