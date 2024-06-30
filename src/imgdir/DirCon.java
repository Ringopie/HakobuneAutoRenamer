package imgdir;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class DirCon extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPanel previewPanel;
	private JButton openButton;
	private JButton saveButton;
	private ButtonGroup langGroup;
	private JProgressBar totalProgressBar;
	private List<FilePreviewPanel> prevPaneList;
	private File saveDirectory;
	private final double VERSION = 1.00;
	private boolean select;
	private ExecutorService threadPool;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				DirCon dCon = new DirCon();
				dCon.setVisible(true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"エラーが発生しました。\n"
						+ "開発者にエラーログを提示してください。\n"
						+ e.getMessage(), "エラー発生",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public DirCon() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("はこぶねオートリネーマー ver. " + VERSION);
		setMinimumSize(new Dimension(437, 316));
		initializeComponents();
		threadPool = Executors.newFixedThreadPool(4);
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * 各種コンポーネントを準備します。
	 */
	private void initializeComponents() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		openButton = new JButton("画像を開く");
		openButton.addActionListener(this);
		topPanel.add(openButton);

		langGroup = new ButtonGroup();
		addRadioButton(topPanel, "JP");
		addRadioButton(topPanel, "EN");
		addRadioButton(topPanel, "TW");
		addRadioButton(topPanel, "CN");
		addRadioButton(topPanel, "KR");

		saveButton = new JButton("保存");
		saveButton.setEnabled(false);
		saveButton.addActionListener(this);
		topPanel.add(saveButton);
		contentPane.add(topPanel, BorderLayout.NORTH);

		previewPanel = new JPanel();
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(previewPanel);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		totalProgressBar = new JProgressBar();
		totalProgressBar.setStringPainted(true);
		contentPane.add(totalProgressBar, BorderLayout.SOUTH);

		prevPaneList = new ArrayList<>();
	}
	/**
	 * ラジオボタンの作成・グループ化・追加を行います。
	 * @param panel
	 * @param text
	 */
	private void addRadioButton(JPanel panel, String text) {
		JRadioButton radioButton = new JRadioButton(text);
		if(text.equals("JP")) {
			radioButton.setSelected(true);
		}
		langGroup.add(radioButton);
		panel.add(radioButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			openImages();
		} else if (e.getSource() == saveButton) {
			saveDirectory = chooseSaveDirectory();
			if(select)	saveImages();
		}
	}
	/**
	 * 画像を選択するためのウィンドウを開き、選択したファイルをloadImageAndRecognizeText()に渡します。
	 */
	private void openImages() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		// 拡張子を定義する(増やす予定)
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
		fileChooser.setFileFilter(filter);
		// 画像選択画面を表示する
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			loadImageAndRecognizeText(selectedFiles);
			saveButton.setEnabled(true);
		} else {
			JOptionPane.showMessageDialog(null, 
					"キャンセルしました", 
					"キャンセル", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	/**
	 * 非同期で複数の画像を読み込み、プレビューパネルを生成します。
	 * @param files
	 */
	private void loadImageAndRecognizeText(File[] files) {
		totalProgressBar.setMaximum(files.length);
		totalProgressBar.setValue(0);
		totalProgressBar.setString("0%");

		for (File file : files) {
			threadPool.submit(() -> {
				try {
					BufferedImage image = ImageIO.read(file);
					String resultText = recognizeText(image);
					SwingUtilities.invokeLater(() -> {
						FilePreviewPanel fpPanel = new FilePreviewPanel(file.getName(), resultText, image);
						prevPaneList.add(fpPanel);
						previewPanel.add(fpPanel);
						contentPane.revalidate();
						contentPane.repaint();

						totalProgressBar.setValue(totalProgressBar.getValue() + 1);
						totalProgressBar.setString((totalProgressBar.getValue() * 100) / files.length + "%");
					});
				} catch (IOException | IllegalStateException e) {
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(null,
								"画像の処理中にエラーが発生しました。\n" + e.getMessage(),
								"エラー発生",
								JOptionPane.ERROR_MESSAGE);
					});
				}
			});
		}
	}


	/**
	 * tessdataディレクトリのパスを検索します。
	 * @return tessdataディレクトリのパス
	 */
	private String getTessDataPath() {
		try {
			File jarDir = new File(DirCon.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()).getParentFile();
			File tessDataDir = new File(jarDir, "tessdata");
			if (tessDataDir.exists() && tessDataDir.isDirectory()) {
				return tessDataDir.getAbsolutePath();
			} else {
				throw new IllegalStateException("tessdataディレクトリが見つかりませんでした。\nディレクトリを削除していないか確認してください。");
			}
		} catch (URISyntaxException uriException) {
			throw new IllegalStateException("JARファイルがあるディレクトリの解析に失敗しました。", uriException);
		}
	}

	/**
	 * 画像をOCR処理して、解読結果を返します。
	 * @param image 処理する画像
	 * @return 解読結果
	 * @throws IllegalStateException OCR処理中にエラーが発生した場合
	 */
	private String recognizeText(BufferedImage image) throws IllegalStateException {
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath(getTessDataPath());
		tesseract.setTessVariable("user_defined_dpi", "500");
		tesseract.setPageSegMode(3);
		tesseract.setLanguage("eng");

		try {
			BufferedImage subImage = image.getSubimage(0, 0, 500, 200);
			String ocrResult = tesseract.doOCR(subImage).replaceAll("opaumon", "OPERATION");

			// 変換
			String resultText = ocrResult
					.replaceAll(" — ", "-")
					.replaceAll("", ocrResult)
					.replaceAll("—","-")
					.replaceAll(" ", "")
					.replaceAll("‘", "")
					.split("\n")[0];
			resultText = resultText.substring(resultText.indexOf('N') + 1)
					.replaceAll(",", "")
					.replaceAll("Q", "7")
					.replaceAll("opammon", "")
					.replaceAll("--", "-")
					.replaceAll("-8-", "-S-")
					.replaceAll("_", "-")
					.toUpperCase();

			Matcher matcher = Pattern.compile("^(.*?-\\w)(\\w+)$").matcher(resultText);
			if (matcher.matches()) {
				resultText = matcher.group(1);
			}

			return resultText;
		} catch (TesseractException | RasterFormatException e) {
			throw new IllegalStateException("画像の処理中にエラーが発生しました。", e);
		}
	}

	/**
	 * 保存先を選ぶウィンドウを表示し、選択したファイルを返します
	 * @return 選択したファイル
	 */
	private File chooseSaveDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("保存先を選んでください");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showSaveDialog(this);
		if (select = returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			JOptionPane.showMessageDialog(null, 
					"キャンセルしました", 
					"キャンセル", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
	}
	/**
	 * 画像の保存処理を行います。
	 */
	private void saveImages() {
		for (FilePreviewPanel prevPane : prevPaneList) {
			if (prevPane.isSelected()) {
				BufferedImage image = prevPane.getImage();
				String text = prevPane.getTextField().getText();
				boolean isHard = prevPane.isHardSelected();

				String languagePrefix = getSelectedLanguage();
				String outputFileName = createOutputFileName(languagePrefix, text, isHard);

				File outputFile = new File(saveDirectory, outputFileName);
				try {
					ImageIO.write(image, "png", outputFile);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, 
							"ファイル名が正しくないおそれがあります。\n\r" 
									+ e.getMessage());
					return;
				}
			}
		}
		JOptionPane.showMessageDialog(this, 
				"画像の保存に成功しました", 
				"完了", JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * 画像の出力ファイル名を生成します。
	 * @param langPrefix
	 * @param text
	 * @param isHard
	 * @return ファイル名
	 */
	private String createOutputFileName(String langPrefix, String text, boolean isHard) {
		String outputFileName = langPrefix + "-" + text;
		if (isHard) {
			outputFileName += "-Hard";
		}
		outputFileName += ".png";

		File outputFile = new File(saveDirectory, outputFileName);
		int count = 2;
		while (outputFile.exists()) {
			outputFileName = langPrefix + "-" + text;
			if (isHard) {
				outputFileName += "-Hard";
			}
			outputFileName +=  "(" + count + ").png";
			outputFile = new File(saveDirectory, outputFileName);
			count++;
		}
		return outputFileName;
	}

	/**
	 * 選択した言語を特定し、その名称を返します。
	 * @return 選択した言語名
	 */
	private String getSelectedLanguage() {
		for(Enumeration<AbstractButton> btns = 
				langGroup.getElements();
				btns.hasMoreElements();
				) {
			AbstractButton btn = btns.nextElement();
			if(btn.isSelected()) {
				return btn.getText();
			}
		}
		// Default: Select JP region.
		return "JP";
	}
}