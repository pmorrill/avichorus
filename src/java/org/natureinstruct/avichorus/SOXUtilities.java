package org.natureinstruct.avichorus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This set of utilities will illustrate basic file manipulations that we have
 * used in Avichorus.
 *
 * Get compiled Sox for windows here:
 * https://sourceforge.net/projects/sox/files/sox/
 *
 * Get compiled libmad.dll here:
 * http://www.opendll.com/index.php?file-download=libmad.dll&arch=32bit&version=&dsc=#
 *
 * @author pmorrill
 */
public class SOXUtilities {

	private static final Double DEFAULT_SP_LENGTH = 60.0;
	private static final String DEFAULT_SP_FREQ = "28";
	private static final String DEFAULT_SP_HEIGHT = "300";
	private static final String DEFAULT_SP_RANGE = "85";
	private static final String DEFAULT_SP_WINDOW = " -w Hamming";

	private final AVCContext ctx;

	public static final Double DEFAULT_SP_RES = 80.0;

	/**
	 * Construct with pointer to the context object
	 *
	 * @param ctx
	 */
	public SOXUtilities(AVCContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Use soxi to determine the length of the recording, and returns as double
	 *
	 * @param fpath
	 * @return
	 */
	protected Double getRecordingLength(String fpath) {
		ArrayList<String> output = new ArrayList();
		if ( 0 == soxi(fpath,"-D",output) ) {
			return Double.parseDouble(output.get(0));
		}
		return 0.0;
	}

	/**
	 * Soxi is a version of the sox program that calculates specific parameters
	 * of a recording file See notes in windows sox deployment guide (link
	 * above)
	 *
	 * @param filep
	 * @param param
	 * @param output
	 * @return
	 */
	protected int soxi(String filep,String param,ArrayList<String> output) {
		String cmd = ctx.getSoxiCmd();
		ProcessBuilder ps = new ProcessBuilder(cmd,param,filep);
		int r = runCmd(ps,output);
		if ( r == 1 ) System.out.println("Return 1 - Error in commandline parameters...");
		if ( r == 2 ) System.out.println("Return 2 - Error in processing command...");
		if ( r == 3 ) System.out.println("Return 3 - Exception in command processing code...");
		return r;
	}

	/**
	 * Convert a wav file to a mp3 file, and store in the temporary spectrogram
	 * path for this recording
	 *
	 * @param rec Recording object
	 * @return
	 */
	protected boolean convertToTempMPEG3(AVCRecording rec) {
		if ( rec.getId() == null || rec.getId() == 0 ) return false;
		if ( !rec.getType().contains("wav") ) return false;

		File f = new File(rec.getPath());
		if ( !f.canRead() ) return false;
		
		String newPath = rec.getSpectrogramPath(ctx) + File.separator + rec.getName().replace("wav","mp3");
		String cmd = ctx.getSoxCmd() + " " + rec.getPath() + " " + newPath;
		ArrayList<String> output = new ArrayList();
		ProcessBuilder ps = new ProcessBuilder(ctx.getSoxCmd(),rec.getPath(),newPath);
		int res = runCmd(ps,output);
		if ( res > 0 ) {
			System.out.println("Error converting file to mp3: " + rec.getPath());
			return false;
		}
		return true;
	}

	/**
	 * Create spectrograms of all types (mono, left channel and right channel),
	 * and store into temp folder for this recording. Spectrograms are created
	 * in 60 second intervals.
	 *
	 * Refer to Sox man page for full details of parameters we use:
	 * http://sox.sourceforge.net/sox.html
	 *
	 * @param rec Recording object
	 * @return
	 */
	protected boolean createSpectrogramsToTemp(AVCRecording rec) {
		if ( rec.getId() == null || rec.getId() == 0 ) return false;

		ArrayList<String> output = new ArrayList();
		/* get file length using soxi - we will partition spectrograms into 60 second segments below */
		Double lenTotal = getRecordingLength(rec.getPath());
		if ( lenTotal != null ) {
			String outPath;
			try {
				if ( lenTotal == 0 ) return false;
				int segs = 1;
				double done = 0, todo = 0;
				Double start = 0.0, process = DEFAULT_SP_LENGTH;

				/* start with a mono version of the png files */
				String ch = "M", remix = "remix -";
				do {
					/* output file segment name */
					outPath = rec.getSpectrogramPath(ctx) + File.separator + ch + segs + ".png";

					if ( process + start > lenTotal ) process = lenTotal - start;

					/* a long list if parameters that will be used to build the SoX process */
					List<String> p = new ArrayList<>();
					p.add(ctx.getSoxCmd());
					p.add(rec.getPath());
					p.add("-n");
					p.add("-V"); // verbose
					p.add("rate");
					p.add(DEFAULT_SP_FREQ + "k");
					p.add("remix");
					p.add("-");
					p.add("trim");
					p.add(start.toString());
					p.add(process.toString());
					p.add("spectrogram");
					p.add("-w"); // window
					p.add("Hamming");
					p.add("-r"); // raw - no legends or axes
					p.add("-s"); // slack overlapping
					p.add("-l"); // light background
					p.add("-m"); // monochrome
					p.add("-X"); // pix per second on X-axis
					p.add(DEFAULT_SP_RES.toString());
					p.add("-z"); // upp limit of X-axis
					p.add(DEFAULT_SP_RANGE);
					p.add("-Y"); // target total height of spectrogram
					p.add(DEFAULT_SP_HEIGHT);
					p.add("-o"); // out path
					p.add(outPath);
					ProcessBuilder ps = new ProcessBuilder(p);

					output.clear();
					Integer r = runCmd(ps,output);
					if ( r > 0 ) {
						System.out.println("Process run error: " + r + "; " + " " + p.toString());
						break;
					}

					segs++;
					start += process;
					if ( start < lenTotal - 1 ) continue;
					switch ( ch ) {
						case "M":
							/* now loop and do a left channel version */
							ch = "L";
							remix = "remix 1";
							start = 0.0;
							process = DEFAULT_SP_LENGTH;
							segs = 1;
							break;
						case "L":
							/* now loop and do a right channel version */
							ch = "R";
							remix = "remix 2";
							start = 0.0;
							process = DEFAULT_SP_LENGTH;
							segs = 1;
							break;
						case "R":
							/* done - break out */
							segs = -1;
					}
				} while ( segs >= 0 );
			} catch (Exception e) { return false; }
		}
		return true;
	}

	/**
	 * http://stackoverflow.com/questions/14542448/capture-the-output-of-an-external-program-in-java
	 * SoX return values are 0 - success; 1 - cmdline parameter problem; 2 -
	 * error in processing
	 *
	 * @param ps
	 * @param output
	 * @return
	 */
	protected int runCmd(ProcessBuilder ps,ArrayList<String> output) {
		Process p;
		BufferedReader is;
		String line;

		ps.redirectErrorStream(true);
		try {
			p = ps.start();
			is = new BufferedReader(new InputStreamReader(p.getInputStream()));
			p.waitFor();
			while ( (line = is.readLine()) != null ) {
				output.add(line);
			}
			return p.exitValue();
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
		}
		return 3;
	}
}
