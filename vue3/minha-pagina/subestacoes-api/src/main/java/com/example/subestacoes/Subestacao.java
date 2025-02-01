
public class Subestacao {
    private int id;
    private String nome;
    private String codigo;
    private double latitude;
    private double longitude;

    public Subestacao(int id, String nome, String codigo, double latitude, double longitude ) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}

