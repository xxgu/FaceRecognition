/**
 * Project             :FaceRecognise project
 * Comments            :�����Ƶ����ʶ�����������
 * Version             :1.0
 * Modification history: number | time |   why  |  who
 * 1 | 2013-4-22 | ���� | jxm
 * 2 | 2013-8-11 | �޸�ͼƬ����Ƶѡ��Ϊ����һ��ѡ�����ļ� | jxm 
 */
package cn.ds.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import cn.ds.domain.VideoTask;
import cn.ds.service.VideoTasksManageThread;
import cn.ds.view.AddVideoProcessTaskView;

public class AddVideoProcessTaskAction implements ActionListener,
		ListSelectionListener, ChangeListener {
	// �����Ƶ����ʶ������
	private AddVideoProcessTaskView addVideoProcessView;
	// �ļ�ѡ��
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	private JFileChooser fileChooser = new JFileChooser();
	// JTableҪ��ʾ�������Ƭ����
	private Vector<Object[]> photoData = new Vector<Object[]>();
	// JTableҪ��ʾ�������Ƶ����
	private Vector<Object[]> videoData = new Vector<Object[]>();
	// ͼƬ��ʽ������
	private FileNameExtensionFilter filterPhoto = new FileNameExtensionFilter(
			"ͼƬ", "jpg", "bmp");
	// ��Ƶ��ʽ������
	private FileNameExtensionFilter filterVideo = new FileNameExtensionFilter(
			"��Ƶ", "avi", "wmv");
	// ��Ƶ����ʶ�������洢·����Ĭ��·��ΪC:\
	private String savePath = "";
	// ��Ҫ�������Ƭ·������
	private List<String> photo = new ArrayList<String>();
	// ��Ҫ�������Ƶ·������
	private List<String> video = new ArrayList<String>();
	// ��ѡ�����Ƭ����ŵ�����
	private int[] photoSelectedRows;
	// ��ѡ�����Ƶ����ŵ�����
	private int[] videoSelectedRows;
	// ֡�ʱ�����Ĭ��ֵ��1.0
	private float magnification;
	// ʶ����ֵ��Ĭ��ֵΪ0.7
	private float threshold;
	// ��Ƶ����ʶ����������߳�
	private VideoTasksManageThread videoTasksManageThread;
	// ��Ƶ����ʶ�����
	private int index;
	// Jtable��ʾ��Ƭ·�������
	private int photoListIndex = 1;
	// Jtable��ʾ��Ƶ·�������
	private int videoListIndex = 1;

	/**
	 * Description :���캯��
	 * 
	 * @param VideoTasksManageThread
	 *            :��Ƶ����ʶ����������߳�
	 * @param index
	 *            ����Ƶ����ʶ�����
	 * @return AddVideoProcessTaskAction
	 */
	public AddVideoProcessTaskAction(
			VideoTasksManageThread videoTasksManageThread, int index) {

		addVideoProcessView = new AddVideoProcessTaskView(this);

		this.videoTasksManageThread = videoTasksManageThread;
		this.index = index;
	}

	/**
	 * Description :�����Ƶ����ʶ�����İ�ť��Ӧ����
	 * 
	 * @param e
	 *            :��Ӧ�¼�
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new AddVideoProcessThread(e.getActionCommand()).start();
	}

	/**
	 * Description :�ڲ��࣬������Ӧ
	 */
	class AddVideoProcessThread extends Thread {
		private String actionCommand;

		public AddVideoProcessThread(String actionCommand) {
			this.actionCommand = actionCommand;
		}

		@Override
		public void run() {
			if (actionCommand.equals("buttonAddPhoto")) {// �����Ƭ��ť��Ӧ
				if (!"".equals(addVideoProcessView.getTextFieldPhotoPath()
						.getText())) {// ����Ƭ·����Ϊ�գ���ʹ�������Ӧ�ļ�ѡ�񴰿�
					fileChooser.setCurrentDirectory(new File(
							addVideoProcessView.getTextFieldPhotoPath()
									.getText()));
				} else {// ����Ƭ·��Ϊ�գ���ʹ�Ĭ�ϵ��ļ�ѡ�񴰿�
					fileChooser.setCurrentDirectory(new File("C:\\"));
				}

				//
				fileChooser.setMultiSelectionEnabled(true);
				// �����ļ�ѡ�������
				fileChooser.setFileFilter(filterPhoto);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fileChooser.showOpenDialog(addVideoProcessView);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							String photoPath = files[i].getAbsolutePath();

							Object[] data = new Object[3];
							data[0] = photoListIndex;
							photoListIndex += 1;
							data[1] = photoPath.substring(photoPath
									.lastIndexOf("\\") + 1);
							data[2] = photoPath;

							photo.add(photoPath);
System.out.println(photoPath);
							photoData.add(data);
						}
						addVideoProcessView.getTextFieldPhotoPath().setText(
								files[files.length - 1].getAbsolutePath());
					}
				}

				// ˢ����Ƭ��ʾ�б�
				refreshTable(photoData, (DefaultTableModel) addVideoProcessView
						.getTablePhoto().getModel());
			} else if (actionCommand.equals("buttonAddVideo")) {// �����Ƶ��ť��Ӧ
				if (!"".equals(addVideoProcessView.getTextFieldVideoPath()
						.getText())) {
					fileChooser.setCurrentDirectory(new File(
							addVideoProcessView.getTextFieldVideoPath()
									.getText()));
				} else {
					fileChooser.setCurrentDirectory(new File("C:\\"));
				}
				fileChooser.setFileFilter(filterVideo);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(true);
				int reVal = fileChooser.showOpenDialog(addVideoProcessView);
				if (reVal == JFileChooser.APPROVE_OPTION) {

					File[] files = fileChooser.getSelectedFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							String videoPath = files[i].getAbsolutePath();

							Object[] data = new Object[3];
							data[0] = videoListIndex;
							videoListIndex += 1;
							data[1] = videoPath.substring(videoPath
									.lastIndexOf("\\") + 1);
							data[2] = videoPath;

							video.add(videoPath);
System.out.println(videoPath);
							videoData.add(data);
						}
						addVideoProcessView.getTextFieldVideoPath().setText(
								files[files.length - 1].getAbsolutePath());
					}

				}
				refreshTable(videoData, (DefaultTableModel) addVideoProcessView
						.getTableVideo().getModel());
			} else if (actionCommand.equals("buttonSave")) {// ���ô������洢·��
				if (!"".equals(addVideoProcessView.getTextFieldSave().getText())) {
					fileChooser.setCurrentDirectory(new File(
							addVideoProcessView.getTextFieldSave().getText()));
				} else {
					fileChooser.setCurrentDirectory(new File("C:\\"));
				}
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int re = fileChooser.showOpenDialog(addVideoProcessView);
				if (re == JFileChooser.APPROVE_OPTION) {
					
					savePath = fileChooser.getSelectedFile().getAbsolutePath();
					if (!savePath.endsWith("\\")) {
						savePath += "\\";
					}
					addVideoProcessView.getTextFieldSave().setText(savePath);
				}
			} else if (actionCommand.equals("buttonSure")) {// ȷ�ϰ�ť��Ӧ

				if (addVideoProcessView.getTablePhoto().getModel()
						.getRowCount() == 0) {
					JOptionPane.showMessageDialog(addVideoProcessView, "δ�����Ƭ");
				} else if (addVideoProcessView.getTableVideo().getModel()
						.getRowCount() == 0) {
					JOptionPane.showMessageDialog(addVideoProcessView, "δ�����Ƶ");
				} else if (savePath.equals("")) {
					JOptionPane
							.showMessageDialog(addVideoProcessView, "����·��Ϊ��");
				} else {
					addVideoProcessView.dispose();

					// ����µ���Ƶ����ʶ������
					VideoTask videoTask = new VideoTask(index, photo, video,
							savePath, magnification, threshold);
					videoTasksManageThread.put(index, videoTask);

					// �����Ƶ����ʶ����������߳��ڵȴ�״̬�ͽ��份��
					if (videoTasksManageThread != null) {
						if (videoTasksManageThread.getState() == State.WAITING) {
							videoTasksManageThread.Resume();
						}
					}
				}

			} else if (actionCommand.equals("buttonDelete")) {// ɾ����ť��Ӧ
				if (photoSelectedRows != null) {
					for (int i = 0; i < photoSelectedRows.length; i++) {
						int id = Integer
								.valueOf(addVideoProcessView.getTablePhoto()
										.getValueAt(photoSelectedRows[i], 0)
										.toString()) - 1;
						photoData.remove(id);
						photo.remove(id);
					}
					// ˢ���б�
					refreshTable(photoData,
							(DefaultTableModel) addVideoProcessView
									.getTablePhoto().getModel());
				}

				if (videoSelectedRows != null) {

					for (int i = 0; i < videoSelectedRows.length; i++) {
						int id = Integer
								.valueOf(addVideoProcessView.getTableVideo()
										.getValueAt(videoSelectedRows[i], 0)
										.toString()) - 1;
						videoData.remove(id);
						video.remove(id);
					}
					refreshTable(videoData,
							(DefaultTableModel) addVideoProcessView
									.getTableVideo().getModel());
				}

			}
		}

		// ˢ���б�
		public void refreshTable(Vector<Object[]> data, DefaultTableModel model) {
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			for (int i = 0; i < data.size(); i++) {
				model.addRow(data.get(i));
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		photoSelectedRows = addVideoProcessView.getTablePhoto()
				.getSelectedRows();
		videoSelectedRows = addVideoProcessView.getTableVideo()
				.getSelectedRows();

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider jSlider = (JSlider) e.getSource();
		if (jSlider.equals(addVideoProcessView.getSliderFPS())) {
			if (jSlider.getValueIsAdjusting()) {
				addVideoProcessView.getLblfps().setText(
						jSlider.getValue() / (float) jSlider.getMaximum()
								+ "*FPS");
			} else {
				magnification = jSlider.getValue()
						/ (float) jSlider.getMaximum();
				addVideoProcessView.getLblfps().setText(magnification + "*FPS");
			}
		} else if (jSlider.equals(addVideoProcessView.getSliderThreshold())) {
			if (jSlider.getValueIsAdjusting()) {
				addVideoProcessView.getLabelThreshold().setText(
						jSlider.getValue() / (float) jSlider.getMaximum() + "");
			} else {
				threshold = jSlider.getValue() / (float) jSlider.getMaximum();
				addVideoProcessView.getLabelThreshold().setText(
						String.valueOf(threshold));
			}
		}

	}

}
