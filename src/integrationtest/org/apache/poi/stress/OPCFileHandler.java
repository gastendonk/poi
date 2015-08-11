/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.stress;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.apache.poi.openxml4j.OpenXML4JTestDataSamples;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.junit.Test;

public class OPCFileHandler extends AbstractFileHandler {
	@Override
    public void handleFile(InputStream stream) throws Exception {
        // ignore password protected files
        if (POIXMLDocumentHandler.isEncrypted(stream)) return;

        InputStream is = OpenXML4JTestDataSamples.openSampleStream("dcterms_bug_56479.zip");
        OPCPackage p = OPCPackage.open(is);
        
        for (PackagePart part : p.getParts()) {
            if (part.getPartName().toString().equals("/docProps/core.xml")) {
                assertEquals(ContentTypes.CORE_PROPERTIES_PART, part.getContentType());
            }
            if (part.getPartName().toString().equals("/word/document.xml")) {
                assertEquals(XWPFRelation.DOCUMENT.getContentType(), part.getContentType());
            }
            if (part.getPartName().toString().equals("/word/theme/theme1.xml")) {
                assertEquals(XWPFRelation.THEME.getContentType(), part.getContentType());
            }
        }
	}
	
    public void handleExtracting(File file) throws Exception {
        // text-extraction is not possible currenlty for these types of files
    }

	// a test-case to test this locally without executing the full TestAllFiles
	@Test
	public void test() throws Exception {
		File file = new File("test-data/openxml4j/dcterms_bug_56479.zip");

		InputStream stream = new PushbackInputStream(new FileInputStream(file), 100000);
		try {
			handleFile(stream);
		} finally {
			stream.close();
		}
		
		handleExtracting(file);
	}
}