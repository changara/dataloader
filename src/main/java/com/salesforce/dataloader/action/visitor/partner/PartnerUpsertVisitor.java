/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.dataloader.action.visitor.partner;

import java.util.List;

import org.apache.commons.beanutils.DynaBean;

import com.salesforce.dataloader.action.progress.ILoaderProgress;
import com.salesforce.dataloader.client.PartnerClient;
import com.salesforce.dataloader.controller.Controller;
import com.salesforce.dataloader.dao.DataWriterInterface;
import com.sforce.ws.ConnectionException;

/**
 * @author Alex Warshavsky
 * @since 8.0
 */
public class PartnerUpsertVisitor extends PartnerLoadVisitor {

    public PartnerUpsertVisitor(Controller controller, ILoaderProgress monitor, DataWriterInterface successWriter,
            DataWriterInterface errorWriter) {
        super(controller, monitor, successWriter, errorWriter);
    }

    @Override
    protected Object[] executeClientAction(PartnerClient client, List<DynaBean> dynabeans) throws ConnectionException {
        return client.loadUpserts(dynabeans);
    }

}
