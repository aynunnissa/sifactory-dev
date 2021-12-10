package apap.TugasAkhir.siFactory.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import apap.TugasAkhir.siFactory.repository.MesinDb;
import apap.TugasAkhir.siFactory.repository.ProduksiDb;
import apap.TugasAkhir.siFactory.repository.RequestUpdateItemDb;
import apap.TugasAkhir.siFactory.rest.ItemDetail;
import apap.TugasAkhir.siFactory.rest.Setting;
import reactor.core.publisher.Mono;
import apap.TugasAkhir.siFactory.model.MesinModel;
import apap.TugasAkhir.siFactory.model.PegawaiModel;
import apap.TugasAkhir.siFactory.model.ProduksiModel;
import apap.TugasAkhir.siFactory.model.RequestUpdateItemModel;
@Service
@Transactional
public class ItemServiceImpl implements ItemService{
    @Autowired
    RequestUpdateItemDb requestUpdateItemDb;

    @Autowired
    MesinDb mesinDb;

    @Autowired
    ProduksiDb produksiDb;

    private final WebClient webClient;

    public ItemServiceImpl(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl(Setting.siItemUrl).build();
    }

    @Override
    public String getKategoryItem (String uuid){
        Mono<ItemDetail> result = this.webClient.get().uri("/api/item/" + uuid).retrieve().bodyToMono(ItemDetail.class);
        String kategori = result.block().getResult().getKategori();
    return kategori;
    }

    @Override
    public Integer getIdKategori(String kategori){
        if(kategori.equals("BUKU")){return 1;}
        else if(kategori.equals("DAPUR")){return 2;}
        else if(kategori.equals("MAKANAN & MINUMAN")){return 3;}
        else if(kategori.equals("ELEKTRONIK")){return 4;}
        else if(kategori.equals("FASHION")){return 5;}
        else if(kategori.equals("KECANTIKAN & PERAWATAN DIRI")){return 6;}
        else if(kategori.equals("FILM & MUSIK")){return 7;}
        else if(kategori.equals("GAMING")){return 8;}
        else if(kategori.equals("GADGET")){return 9;}
        else if(kategori.equals("KESEHATAN")){return 10;}
        else if(kategori.equals("RUMAH TANGGA")){return 11;}
        else if(kategori.equals("FURNITURE")){return 12;}
        else if(kategori.equals("ALAT & PERANGKAT KERAS")){return 13;}
        else if(kategori.equals("WEDDING")){return 14;}
        else {return 0;}
    }

    @Override
    public String updatestok(String uuid, Integer jumlahstok, long idMesin, PegawaiModel pegawai, long ruiId){
        int counterPegawaiAkhir = pegawai.getCounter() + 1;
        MesinModel mesin = mesinDb.getById(idMesin);
        long kapasitasMesinAkhir = mesin.getKapasitas() - 1;
        long idKategori = mesin.getIdKategori();
        HashMap<String, String> data = new HashMap<>();
        int jumlahbarang = Integer.parseInt(this.webClient.get().uri("/api/item/" + uuid).retrieve().bodyToMono(ItemDetail.class).block().getResult().getStok());
        int totalStok = jumlahbarang+jumlahstok;
        data.put("stok", Integer.toString(totalStok));
        Mono<ItemDetail> item = this.webClient.put().uri("/api/item/" + uuid).syncBody(data).retrieve().bodyToMono(ItemDetail.class);
        String status = item.block().getStatus();
        if(status.equals("200")){
            ProduksiModel produksi = new ProduksiModel();
            produksi.setRequestUpdateItem(null);
            if(ruiId > 0){
                int ruiId1 = (int) ruiId;
                Optional<RequestUpdateItemModel> rui = requestUpdateItemDb.findById(ruiId1);
                RequestUpdateItemModel ruiModel = null;
                if(rui.isPresent()){
                    ruiModel = rui.get();
                    produksi.setRequestUpdateItem(ruiModel);
                    ruiModel.setExecuted(true);
                }
            }
            produksi.setPegawai(pegawai);
            produksi.setIdKategori(idKategori);
            produksi.setIdItem(uuid);
            produksi.setTanggalProduksi(LocalDate.now());
            produksi.setTambahanStok((long) jumlahstok);
            produksi.setMesin(mesin);
            produksiDb.save(produksi);
            mesin.setKapasitas(kapasitasMesinAkhir);
            pegawai.setCounter(counterPegawaiAkhir);
            return("berhasil");
        }
        return ("gagal");
    }

    @Override
    public RequestUpdateItemModel getRequestUpdateItem (long ruiId){
        int ruiId1 = (int) ruiId;
        RequestUpdateItemModel rui = requestUpdateItemDb.getById(ruiId1);
        return rui;
    }

    @Override
    public List<RequestUpdateItemModel> getListRequestUpdateItem (){
        return requestUpdateItemDb.findAll();
         
    }

}
