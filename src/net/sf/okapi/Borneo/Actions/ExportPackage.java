/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Borneo.Actions;

import java.io.File;
import java.sql.ResultSet;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Package.IWriter;

public class ExportPackage extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private ExportPackageOptions  m_Opt;
	private String                m_sTarget;
	private DBTarget              m_Trg;
	private IWriter               m_PkgW;
	private IFilterItem           m_SourceFI;
	private IFilterItem           m_TargetFI;
	
	public ExportPackage (FilterAccess p_FA,
		DBBase p_DB)
	{
		m_FA = p_FA;
		m_DB = p_DB;
		m_Doc = null;
		m_Opt = new ExportPackageOptions();
	}

	@Override
	public boolean execute (int[] p_aDKeys,
		String[] p_aTargets)
	{
		try {
			m_nDocTotal = p_aDKeys.length;
			m_nCurrentDoc = 0;
			String sStorageRoot = m_DB.getStorage() + File.separator + DBBase.DIR_EXCHANGE;

			for ( int i=0; i<p_aTargets.length; i++ ) {
				// For each target
				m_sTarget = p_aTargets[i];
				m_FA.getLog().message(Res.getString("TRG_LANGUAGE") + m_sTarget);
				m_Trg = m_DB.getTargetData(m_sTarget);
			
				// Instantiates a package writer
				//TODO: make it dynamic, using a plug-in model
				if ( m_Opt.getPackageType().equals("omegat") )
					m_PkgW = new net.sf.okapi.Package.OmegaT.Writer(m_FA.getLog());
				else if ( m_Opt.getPackageType().equals("ttx") )
					m_PkgW = new net.sf.okapi.Package.ttx.Writer(m_FA.getLog());
				else if ( m_Opt.getPackageType().equals("xliff") )				
					m_PkgW = new net.sf.okapi.Package.XLIFF.Writer(m_FA.getLog());
				else throw new Exception("Package type unknown: "+m_Opt.getPackageType());
				
				// Start the package
				String[] aRes = m_Opt.makePackageName(m_DB.getProjectID(), m_sTarget);
				m_PkgW.setParameters(m_DB.getSourceLanguage(), m_sTarget, m_DB.getProjectID(),
					sStorageRoot + File.separator + aRes[1], aRes[0]);
				m_PkgW.writeStartPackage();
				
				// For each documents
				for ( int j=0; j<m_nDocTotal; j++ ) {
					if ( !processDocument(p_aDKeys[j]) ) return false;
					m_FA.getLog().setLog(LogType.MAINPROGRESS,
						Utils.getPercentage(++m_nCurrentDoc, m_nDocTotal), null);
				}
				
				// End the package
				m_PkgW.writeEndPackage(m_Opt.getCreateZip());
			}
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public String getID () {
		return ID_EXPORTPACKAGE;
	}

	@Override
	public String getName () {
		return "Export Translation Package";
	}

	@Override
	public IParameters getOptions () {
		return m_Opt;
	}

	@Override
	public boolean hasOptions () {
		return true;
	}

	@Override
	public boolean needsTarget () {
		return true;
	}

	@Override
	public void setOptions (IParameters p_Value) {
		m_Opt = (ExportPackageOptions)p_Value;
	}

	@Override
	public boolean start () {
		if ( !m_FA.getLog().beginTask(getName()) ) return false;
		m_SourceFI = new FilterItem();
		m_TargetFI = new FilterItem();
		return true;
	}
	
	@Override
	public void stop () {
		m_FA.getLog().endTask(null);
	}
	
	private boolean processDocument (int p_nDKey)
	{
		boolean         bContinue = true;
		boolean         bDoEnd = false;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, m_sTarget);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			
			if ( m_Doc.getTargetDoc().isExcluded() )
			{
				m_FA.getLog().message(String.format(Res.getString("TARGETEXCLUDEDFORTHISDOC"), m_sTarget));
				return bContinue;
			}
			bDoEnd = true;
			
			// Compute the output path
			String sOutRelativePath = m_Doc.getTargetDoc().getFullPath(m_Doc, m_Trg,
				m_DB.getSourceRoot(), m_sTarget);
			// Adjust the root
			if (( m_Trg.getRoot() == null ) || ( m_Trg.getRoot().length() == 0 ))
				sOutRelativePath = sOutRelativePath.substring(m_DB.getSourceRoot().length()+1);
			else
				sOutRelativePath = sOutRelativePath.substring(m_Trg.getRoot().length()+1);
//			String sOutFullPath = m_PkgW.getDirectory() + File.separator + sOutRelativePath;
			
			m_PkgW.createDocument(p_nDKey, sOutRelativePath);
			m_PkgW.writeStartDocument(m_Doc.getRelativePath());

			int nTStatus;
			boolean bTranslate;
			String sTrg;
			ResultSet rsTrg = m_DB.fetchTargetItems(p_nDKey, m_sTarget);
			while ( rsTrg.next() ) {
				nTStatus = rsTrg.getInt(DBBase.TRGCOLI_STATUS);
				if ( nTStatus == IFilterItem.TSTATUS_UNUSED ) continue;

				m_SourceFI.reset();
				m_SourceFI.setItemType(FilterItemType.TEXT);
				m_SourceFI.setItemID(rsTrg.getInt(DBBase.TRGCOLI_KEY));
				m_SourceFI.setCodeMapping(rsTrg.getString(DBBase.TRGCOLI_SCODES));
				m_SourceFI.modifyText(rsTrg.getString(DBBase.TRGCOLI_STEXT));
				m_SourceFI.setResName(rsTrg.getString(DBBase.TRGCOLI_RESNAME));
				m_SourceFI.setResType(rsTrg.getString(DBBase.TRGCOLI_RESTYPE));
				
				m_TargetFI.reset();
				m_TargetFI.setCodeMapping(rsTrg.getString(DBBase.TRGCOLI_SCODES));
				
				bTranslate = (nTStatus != IFilterItem.TSTATUS_NOTRANS);
				//if ( !bTranslate && !m_Opt.IncludeNTItems ) continue;
				m_SourceFI.setTranslatable(bTranslate);

				sTrg = rsTrg.getString(DBBase.TRGCOLI_TTEXT);
				if ( !m_SourceFI.isEmpty() && ((sTrg != null ) && ( sTrg.length() != 0)) ) {
					m_TargetFI.modifyText(sTrg);
					m_SourceFI.setTranslated(bTranslate);
				}
				// Else writer will use the source
				m_PkgW.writeItem(m_SourceFI, m_TargetFI, nTStatus);
			}
			
			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
			if ( bDoEnd ) {
				m_PkgW.writeEndDocument();
				try {
					m_Doc.getTargetDoc().setStatus(String.format("%s / %s", getName(),
						(bContinue ? Res.getString("ACTION_DONE")
						: Res.getString(Res.getString("ACTION_ERROR")))));
					m_DB.updateTargetDocumentStatus(m_Doc);
				}
				catch ( Exception E ) {
					m_FA.getLog().error(E.getLocalizedMessage());
					bContinue = false;
				}
			}
		}
		
		return bContinue;
	}

}
