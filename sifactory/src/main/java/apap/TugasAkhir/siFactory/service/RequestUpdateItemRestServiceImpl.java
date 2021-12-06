package apap.TugasAkhir.siFactory.service;

import apap.TugasAkhir.siFactory.model.RequestUpdateItemModel;
import apap.TugasAkhir.siFactory.repository.RequestUpdateItemDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RequestUpdateItemRestServiceImpl implements RequestUpdateItemRestService {

    @Autowired
    private RequestUpdateItemDb requestUpdateItemDb;

    @Override
    public RequestUpdateItemModel createRequestUpdateItem(RequestUpdateItemModel requestUpdateItemModel) {
        requestUpdateItemModel.setExecuted(false);
        requestUpdateItemModel.setDeliveryModel(null);
        return requestUpdateItemDb.save(requestUpdateItemModel);
    }
}
