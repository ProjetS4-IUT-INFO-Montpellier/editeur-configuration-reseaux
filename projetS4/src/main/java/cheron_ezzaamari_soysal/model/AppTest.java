package cheron_ezzaamari_soysal.model;

public class AppTest {
    public static void main(String[] args) {
        /*int a = IdReseau.getIdReseau();
        int b = IdReseau.getIdReseau();
        int c = IdReseau.getIdReseau();
        int d = IdReseau.getInstance().getIdReseau();
        IdReseau.getInstance().increment();
        int e = IdReseau.getInstance().getIdReseau();

        System.out.println(a);
        System.out.println(d);
        System.out.println(e);
         */
        


        Reseau resa = new ReseauCompose("105497784557");
        int a = resa.getId();


        Reseau resb = new SousReseau("105497784557");
        int b = resb.getId();
        System.out.println(a);
        System.out.println(b);
    }


}
