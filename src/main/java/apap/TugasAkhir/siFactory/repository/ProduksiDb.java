package apap.TugasAkhir.siFactory.repository;

import apap.TugasAkhir.siFactory.model.ProduksiModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ProduksiDb extends JpaRepository<ProduksiModel,Long> {
    List<ProduksiModel> findAllByOrderByIdProduksiAsc();

}
