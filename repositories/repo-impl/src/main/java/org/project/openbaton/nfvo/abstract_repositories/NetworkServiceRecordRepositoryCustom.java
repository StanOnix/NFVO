package org.project.openbaton.nfvo.abstract_repositories;

import org.project.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;

/**
 * Created by mob on 04.09.15.
 */
public interface NetworkServiceRecordRepositoryCustom {
    void addVnfr(VirtualNetworkFunctionRecord vnfr, String id);
}
