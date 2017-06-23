package fr.ifremer.nemo.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.domain.ConversionDomain;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.mapping.Mapping;
import fr.ifremer.nemo.mapping.MappingTable;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.output.NemoWriterFactory;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;

public class SWTProcess implements IRunnableWithProgress {

	public static ProgressNotification notifier;

	private NemoWriter writer;
	private Mapping mapping;
	private final MappingTable MappingTable = new MappingTable();

	@SuppressWarnings("unused")
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		InputDomain iDomain = Nemo.getInstance().getDomain();
		CruiseDomain cDomain = iDomain.getCruise();
		StationDomain sDomain = iDomain.getStation();
		ConversionDomain convDomain = iDomain.getConversion();

		Configuration config = Nemo.getInstance().getConfig();

		Conversion formatConversion = Conversion.MEDATLAS;
		String modus = Mapping.MODUS_MULTISTATIONS;

		if (convDomain.getCurrent() == E_TypeConversion.ODV_MONOSTATION
				|| convDomain.getCurrent() == E_TypeConversion.ODV_MULTISTATIONS)
			formatConversion = Conversion.ODV;

		if (convDomain.getCurrent() == E_TypeConversion.NETCDF_MONOSTATION
				|| convDomain.getCurrent() == E_TypeConversion.NETCDF_MULTISTATIONS)
			formatConversion = Conversion.NETCDF;
		
		
		
		if (convDomain.getCurrent() == E_TypeConversion.ODV_MONOSTATION
				|| convDomain.getCurrent() == E_TypeConversion.NETCDF_MONOSTATION) {
			modus = Mapping.MODUS_MONOSTATION;
		}
		

		try {
//			System.out.println("Conversion START at " + new Date()); //$NON-NLS-1$
			if (Nemo.getInstance().getLogger().isDebugEnabled()){
				Nemo.getInstance().getLogger().info("Conversion START at " + new Date());}
			
			monitor.beginTask(Messages.SWTProcess_1, IProgressMonitor.UNKNOWN);

			if (!monitor.isCanceled()) {

				// Ecriture des fichiers
				monitor.setTaskName(Messages.SWTProcess_4);
				writer = NemoWriterFactory.createWriter(formatConversion);
				writer.write(notifier);

				// mapping
				if (config.isWriteMapping()) {
					monitor.setTaskName("enregistrement du mapping"); //TODO msg
					String localCdiId;
					for (String outputPath : iDomain.getMedia().getOutputFiles().keySet()) {
						File outputFile = new File(outputPath);
						for (String local_cdi_id : iDomain.getMedia().getOutputFiles().get(outputPath)) {
							
							Station st = CDIResumeWriter.getInstance().getStationByLocalCdiId(local_cdi_id);
							mapping = new Mapping(local_cdi_id, formatConversion.getFormatName());
							mapping.setFilename(convDomain.getFileNameInCoupling(outputFile.getAbsolutePath()));
							mapping.setDate(new Date());
							mapping.setModus(modus);

							MappingTable.persist(mapping);
						}
					}
				}

				// Ecriture du resume CDI
				if (config.isWriteCdi()) {
					monitor.setTaskName(Messages.SWTProcess_CDIresume);
					CDIResumeWriter.getInstance().writeResume(formatConversion);
				}
			}

//			System.out.println("Conversion END   at " + new Date() + "\n---------------"); //$NON-NLS-1$ //$NON-NLS-2$
			if (Nemo.getInstance().getLogger().isDebugEnabled()){
			Nemo.getInstance().getLogger().info("Conversion END   at " + new Date() + "\n---------------");} //$NON-NLS-1$ //$NON-NLS-2$
			monitor.done();
		} catch (Exception e) {
			throw new InterruptedException(Messages.SWTProcess_5 + e.getMessage());
		}
	}
}
