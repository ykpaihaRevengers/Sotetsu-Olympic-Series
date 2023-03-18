package filemng.bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import home.tool.ScenarioUtil;

public class FileList {

	private List<TextBook> bookList;
	private Path dirPath;
	private List<FileEdition> fileEditions;

	public FileList(Path dirPath, List<TextBook> bookList) {
		super();
		this.bookList = bookList;
		this.dirPath = dirPath;
	}

	public FileList(Path dirPath) {
		super();

		List<TextBook> bookList = new ArrayList<>();

		try {
			if (Files.isDirectory(dirPath)) {
				Files.list(dirPath).forEach(new Consumer<Path>() {
					@Override
					public void accept(Path filePath) {
						if (!Files.isDirectory(filePath)) {
							System.out.println("ファイル：" + filePath);
							TextBook textBook = new TextBook(filePath);
							//ファイルを分割した場合
							if (ScenarioUtil.checkList(textBook.getFileTextSeveral())) {
								textBook.getFileTextSeveral().stream().forEachOrdered(fileList -> bookList.add(new TextBook(fileList, filePath)));
							} else {
								bookList.add(textBook);
							}
						}
					}
				});
				this.dirPath = dirPath;
			} else {
				TextBook textBook = new TextBook(dirPath);
				//ファイルを分割した場合
				if (ScenarioUtil.checkList(textBook.getFileTextSeveral())) {
					textBook.getFileTextSeveral().stream().forEachOrdered(fileList -> bookList.add(new TextBook(fileList, dirPath)));
				} else {
					bookList.add(textBook);
				}

				this.dirPath = dirPath.getParent();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bookList = bookList;
	}

	public List<TextBook> getBookList() {
		return bookList;
	}

	public void setBookList(List<TextBook> bookList) {
		this.bookList = bookList;
	}

	public Path getDirPath() {
		return dirPath;
	}

	public List<FileEdition> getFileEditions() {
		return fileEditions;
	}

	public void setFileEditions(List<FileEdition> fileEditions) {
		this.fileEditions = fileEditions;
	}

}
