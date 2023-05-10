package International_Trade_Union.node;

import java.util.HashSet;
import java.util.Set;

public class Nodes {

    public int size(){
        return urlAddress.size();
    }

    public Nodes() {
        this.urlAddress = new HashSet<>();
    }

    public Nodes(Set<String> urlAddress) {
        this.urlAddress = urlAddress;
    }

    private  Set<String> urlAddress = new HashSet<>();

    public  void setUrlAddress(Set<String> urlAddress) {
        this.urlAddress = urlAddress;
    }
    public void addAddress(String addressUrl){
        urlAddress.add(addressUrl);
    }

    public  Set<String> getUrlAddresses() {
        return urlAddress;
    }

    public void clear(){
        urlAddress = new HashSet<>();
    }
}
