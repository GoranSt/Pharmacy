/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apteka;

/**
 *
 * @author gk77
 */
import java.util.*;


 
 
class SLLNode<E> {
        SLLNode<E> succ;
        E element;
 
        public SLLNode(SLLNode<E> succ, E element) {
                this.succ = succ;
                this.element = element;
        }
 
}
 
class MapEntry<K extends Comparable<K>, E> implements Comparable<K> {
        // Each MapEntry object is a pair consisting of a key (a Comparable
        // object) and a value (an arbitrary object).
        K key;
        E value;
 
        public MapEntry(K key, E val) {
                this.key = key;
                this.value = val;
        }
 
        public int compareTo(K that) {
                // Compare this map entry to that map entry.
                @SuppressWarnings("unchecked")
                MapEntry<K, E> other = (MapEntry<K, E>) that;
                return this.key.compareTo(other.key);
        }
 
        public String toString() {
                return "<" + key + "," + value + ">";
        }
}
 
class CBHT<K extends Comparable<K>, E> {
        private SLLNode<MapEntry<K, E>>[] buckets;
 
        @SuppressWarnings("unchecked")
        public CBHT(int m) {
                // Construct an empty CBHT with m buckets.
                buckets = (SLLNode<MapEntry<K, E>>[]) new SLLNode[m];
        }
 
        private int hash(K key) {
                // Translate key to an index of the array buckets.
                return Math.abs(key.hashCode()) % buckets.length;
        }
 
        public SLLNode<MapEntry<K, E>> search(K targetKey) {
                // Find which if any node of this CBHT contains an
                // entry whose key is equal to targetKey.
                // Return a link to that node (or null if there is none).
                int b = hash(targetKey);
                for (SLLNode<MapEntry<K, E>> curr = buckets[b]; curr != null; curr = curr.succ) {
                        if (targetKey.equals(((MapEntry<K, E>) curr.element).key))
                                return curr;
                }
                return null;
        }
 
        public void insert(K key, E val) {
                // Insert the entry <key, val> into this CBHT.
                MapEntry<K, E> newEntry = new MapEntry<K, E>(key, val);
                int b = hash(key);
                for (SLLNode<MapEntry<K, E>> curr = buckets[b]; curr != null; curr = curr.succ) {
                        if (key.equals(((MapEntry<K, E>) curr.element).key)) {
                                // Make newEntry replace the existing entry ...
                                curr.element = newEntry;
                                return;
                        }
                }
                // Insert newEntry at the front of the 1WLL in bucket b ...
                buckets[b] = new SLLNode<MapEntry<K, E>>(buckets[b], newEntry);
        }
 
        public void delete(K key) {
                // Delete the entry (if any) whose key is equal
                // to key from this CBHT.
                int b = hash(key);
                for (SLLNode<MapEntry<K, E>> pred = null, curr = buckets[b]; curr != null; pred = curr, curr = curr.succ) {
                        if (key.equals(((MapEntry<K, E>) curr.element).key)) {
                                if (pred == null)
                                        buckets[b] = curr.succ;
                                else
                                        pred.succ = curr.succ;
                                return;
                        }
                }
        }
}
 
class Key implements Comparable<Key> {
  /* мора да се имплементираат (override, со Object кako аргумент) compareto i equals методите за да се споредат клучевите (дали постои таков клуч...се прави споредба). Во спротивно, ако не се напишат или се напишат со аргумент објект ко оној што се споредува (пример методот equals во Key има аргумент Key), нема да се override - не методот. Во тој случај, 
    тој воопшто нема да биде повикан и за споредба ќе се искористат equals/compareTo од Object класата.
         * */
        private String name;
 
        public Key(String name) {
                this.name = name;
        }
 
        @Override
        public int hashCode() {
                int sum = 0;
                for (int i = 0; i < name.length(); i++)
                        sum += (int) name.charAt(i);
                return ((29 * (29 * (29 + sum))) % 102780);
        }
 
        @Override
        public boolean equals(Object obj) {
                if (obj == null || this.getClass() != obj.getClass())
                        return false;
                Key key = (Key) obj;
                return name.equals(key.name);
        }
 
        @Override
        public int compareTo(Key o) {
                if (o == null)
                        return 1;
                return name.compareTo(o.name);
        }
 
}
 
class Drug {
        private String name;
        private String bool;
        private int price;
        int quantity;
 
        public Drug(String name, int b, int price, int quantity) {
                this.name = name;
                if (b == 0)
                        this.bool = "NEG";
                else
                        this.bool = "POZ";
                this.price = price;
                this.quantity = quantity;
        }
 
        public boolean buy(int amount) {
                if (quantity < amount)
                        return false;
                else {
                        quantity -= amount;
                        return true;
                }
        }
 
        public String toString() {
                return String.format("%s\n%s\n%d", name, bool, price);
        }
}
 
public class Apteka {
        public static void main(String[] args) {
                Scanner in = new Scanner(System.in);
                int n = Integer.parseInt(in.nextLine());
                CBHT<Key, Drug> map = new CBHT<Key, Drug>(2 * n);
                String[] splited = null;
                for (int i = 0; i < n; i++) {
                        splited = in.nextLine().split("\\s++");// IME(0) POZ/NEG(1) CENA(2) KOLICINA(3)
                        Drug d = new Drug(splited[0].toUpperCase(),
                                        Integer.parseInt(splited[1]), Integer.parseInt(splited[2]),
                                        Integer.parseInt(splited[3]));
                        map.insert(new Key(splited[0].toUpperCase()), d);
                }
                System.out.println("<---- ORDER ---->");
                while (in.hasNextLine()) {
                        String readDRUG = in.nextLine();
                        if (readDRUG.equals("KRAJ"))
                                break;
                        int amount = Integer.parseInt(in.nextLine());
                        SLLNode<MapEntry<Key, Drug>> node = map.search(new Key(readDRUG
                                        .toUpperCase().trim()));
                        if (node == null)
                                System.out.println("Nema takov lek!");
                        else {
                                Drug d = node.element.value;
                                if (!d.buy(amount))
                                        System.out.println(d.toString() + "\n" + d.quantity
                                                        + "\nNema dovolno lekovi\n");
                                else
                                        System.out.println(d.toString() + "\n" + splited[3]
                                                        + "\nNapravena naracka\n");
                        }
                }
        }
}
