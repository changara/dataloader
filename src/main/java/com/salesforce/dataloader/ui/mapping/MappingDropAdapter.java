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

package com.salesforce.dataloader.ui.mapping;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.salesforce.dataloader.ui.MappingDialog;

/**
 * Describe your class here.
 *
 * @author Lexi Viripaeff
 * @since 6.0
 */
public class MappingDropAdapter extends ViewerDropAdapter {

    private final MappingDialog dlg;

    public MappingDropAdapter(TableViewer arg0, MappingDialog dlg) {
        super(arg0);
        this.dlg = dlg;
    }

    @Override
    public boolean performDrop(Object arg0) {
        @SuppressWarnings("unchecked")
        Map.Entry<String, String> entry = (Entry<String, String>)getCurrentTarget();

        //replenish the old
        String oldSforce = entry.getValue();
        if (oldSforce != null && oldSforce.length() > 0) {
            dlg.replenishSforceField(oldSforce);
        }

        entry.setValue((String)arg0);
        dlg.getMapper().putMapping(entry.getKey(), entry.getValue());
        dlg.packMappingColumns();
        return true;
    }

    @Override
    public boolean validateDrop(Object arg0, int arg1, TransferData arg2) {

        return TextTransfer.getInstance().isSupportedType(arg2);
    }

}