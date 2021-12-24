package nl.hva.ict.data.MongoDB;

import nl.hva.ict.MainApplication;
import nl.hva.ict.models.Landen;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

/**
 * Landen informatie ophalen van de MongoDB
 */
public class MongoLandeninformatie extends MongoDB {

    private final List<Landen> landen;

    /**
     * Constructor
     */
    public MongoLandeninformatie() {
        // Init arraylist
        landen = new ArrayList<>();
    }

    /**
     * Haal alle landen op die in de arraylijst zitten
     * @return arraylijst met landen
     */
    @Override
    public List getAll() {
        return landen;
    }

    /**
     * Haal 1 object op. Niet gebruikt in deze class maar door de interface data wel verplicht
     * @return een object
     */
    @Override
    public Object get() {
        return null;
    }

    /**
     * Voeg een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void add(Object object) {

    }

    /**
     * Update een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void update(Object object) {

    }

    /**
     * Verwijder een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void remove(Object object) {

    }

    /**
     * Haal alle informatie op uit de NoSQL server over welke landen een bepaalde taal spreken. Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param taal Welke taal wil je weten
     * @param alleenAfrika filter het resultaat zodat wel of niet alleen afrikaanse landen terug komen
     */
    public void wieSpreekt(String taal, boolean alleenAfrika) {

        // Als je geen NoSQL server hebt opgegeven gaat de methode niet verder anders zou je een nullpointer krijgen
        if (MainApplication.getNosqlHost().equals(""))
            return;

        // reset arraylist
        this.landen.clear();

        // selecteer collection
        this.selectedCollection("landen");

        if (alleenAfrika) {

            // Aggregation functie in Mongo
            Bson match = match(eq("region", "Africa"));
            List<Document> results = collection.aggregate(Arrays.asList(match))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }
        } else {
            // Aggregation functie in Mongo
            Bson match = match(eq("languages.name", taal));
            List<Document> results = collection.aggregate(Arrays.asList(match))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }
        }
    }

    /**
     * Haal alle informatie op uit de NoSQL server in welke landen je met een bepaalde valuta kan betalen. Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param valuta Welke valuta wil je weten
     * @param alleenAfrika filter het resultaat zodat wel of niet alleen afrikaanse landen terug komen
     */
    public void waarBetaalJeMet(String valuta, boolean alleenAfrika) {
        // Als je geen NoSQL server hebt opgegeven gaat de methode niet verder anders zou je een nullpointer krijgen
        if (MainApplication.getNosqlHost().equals(""))
            return;

        // reset arraylist
        this.landen.clear();

        // selecteer collection
        this.selectedCollection("landen");

        if (alleenAfrika) {

            // Aggregation functie in Mongo
            Bson match = match(eq("region", "Africa"));
            Bson match2 = match(eq("currencies.name", valuta));

            List<Document> results = collection.aggregate(Arrays.asList(match, match2))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }

        } else {
            Bson match = match(eq("currencies.name", valuta));

            List<Document> results = collection.aggregate(Arrays.asList(match))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }


        }
    }

    /**
     * Welke landen zijn er in welk werelddeel. Haal deze informatie uit de database
     * . Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param werelddeel Welke valuta wil je weten
     */
    public void welkeLandenZijnErIn(String werelddeel) {

        // Als je geen NoSQL server hebt opgegeven gaat de methode niet verder anders zou je een nullpointer krijgen
        if (MainApplication.getNosqlHost().equals(""))
            return;

        // reset arraylist
        this.landen.clear();

        // selecteer collection
        this.selectedCollection("landen");

        if (werelddeel == "Africa") {
            Bson match = match(eq("region", werelddeel));

            List<Document> results = collection.aggregate(Arrays.asList(match))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }
        } else {
            Bson match = match(eq("subregion", werelddeel));

            List<Document> results = collection.aggregate(Arrays.asList(match))
                    .into(new ArrayList<>());

            // Maak models en voeg resultaat toe aan arraylist
            for (Document land : results) {
                this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
            }
        }
    }

    /**
     * Hoeveel inwoners heeft Oost-Afrika?. Haal deze informatie uit de database en gebruik hiervoor aggregation.
     */
    public int hoeveelInwonersOostAfrika() {
        // Als je geen NoSQL server hebt opgegeven gaat de methode niet verder anders zou je een nullpointer krijgen
        if (MainApplication.getNosqlHost().equals(""))
            return 0;

        // reset arraylist
        this.landen.clear();

        // selecteer collection
        this.selectedCollection("landen");

        List<Document> results = collection.aggregate(Arrays.asList(new Document("$match",
                                new Document("subregion", "Eastern Africa")),
                        new Document("$group",
                                new Document("_id", "$name")
                                        .append("sum",
                                                new Document("$sum", "$population"))),
                        new Document("$group",
                                new Document("_id",
                                        null)
                                        .append("population",
                                                new Document("$sum", "$sum"))),
                        new Document("$project",
                                new Document("population", 1L)
                                        .append("_id", 0L))))
                .into(new ArrayList<>());

//        System.out.println(results.get(0).get("population"));

        String totalPopulation =  results.get(0).get("population").toString();
        return new BigDecimal(totalPopulation).intValue();
    }
}
