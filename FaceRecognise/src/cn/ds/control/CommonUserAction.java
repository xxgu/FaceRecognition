/**
 * Project             :FaceRecognise project
 * Comments            :��ͨ�û�������
 * Version             :1.0
 * Modification history: number | time |   why  |  who
 * 1 | 2013-5-20 | ���� | jxm
 * 2 | 2013-8-11 | ���˫������Ƶʶ�����ļ���| jxm 
 */
package cn.ds.control;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;






import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import cn.ds.domain.User;
import cn.ds.domain.VideoTask;
import cn.ds.model.CommonUserModel;
import cn.ds.service.RecogniseService;
import cn.ds.service.ScheduleManage;
import cn.ds.service.VideoTasksManageThread;
import cn.ds.view.CommonUserView;

public class CommonUserAction implements ActionListener, ChangeListener,
		MouseListener {

	// ��ͨ�û�����
	private CommonUserView commonUserView;
	// ���ȣ���ʾ������
	private ScheduleManage scheduleManage = null;
	// ��Ƶ�������񼯺�
	private Map<Integer, VideoTask> videoTasks = new HashMap<Integer, VideoTask>();
	// ���ڴ�������񼯺�
	private Map<Integer, VideoTask> runningVideoTasks = new HashMap<Integer, VideoTask>();
	// ��Ƶ����ʶ���������
	private VideoTasksManageThread videoTasksManageThread = null;
	// �����Ƶ����ʶ��������
	private int maxVideoTaskNum = 2;
	// ��Ƶ����ʶ���������
	private int videoTaskIndex = 0;
	// ��ǰ�û�
	private User user;
	// ��ͨ�û�����ģ��
	private CommonUserModel commonUserModel;

	/**
	 * Description :���캯��
	 * 
	 * @param id
	 *            :�û�id
	 * @param mode
	 *            ����½ģʽ
	 * @return CommonUserAction
	 */
	public CommonUserAction(int id, int mode, BlockingQueue<Boolean> result) {

		commonUserModel = new CommonUserModel();

		// �����û�ID�õ��û�
		user = commonUserModel.getUserById(id);

		if (mode == 0) {// "����ģʽ"��½�����Ը����û���Ϣ
			commonUserView = new CommonUserView(this, user);
		} else {// ��ͨģʽ��ֻ����ʹ����Ƶ����ʶ����
			commonUserView = new CommonUserView(this, null);
		}
		commonUserView.setTitle("��ͨ�û�:" + user.getName());

		// ��дcommonUserView���ڵĹرշ���
		commonUserView.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (RecogniseService.getInstance() != null) {

					// ����RecogniseService�߳�
					RecogniseService.getInstance().setFlag(false);

					// �ͷ�����ͷcapture��Դ
					RecogniseService.getInstance().releaseCapture();
				}
				System.exit(0);
			}
		});

		try {
			result.put(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description :��ӦTabbedPane��ǩ�ı�
	 * 
	 * @param e
	 *            :��Ӧ�¼�
	 * @return void
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (commonUserView.getTabbedPane().getSelectedIndex() == 1) {// �л�����Ƶ����ʶ���������
			if (scheduleManage == null) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						// �������ȣ���ʾ�������߳�
						scheduleManage = new ScheduleManage(videoTasks,
								(DefaultTableModel) commonUserView
										.getTableVideoProcessTask().getModel());
						scheduleManage.start();

						// ������������߳�
						videoTasksManageThread = new VideoTasksManageThread(
								videoTasks, runningVideoTasks, scheduleManage,
								maxVideoTaskNum);
						videoTasksManageThread.start();

					}
				}).start();
			}

		}
	}

	/**
	 * Description :��ͨ�û��������İ�ť��Ӧ����
	 * 
	 * @param e
	 *            :��Ӧ�¼�
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		new CommonUserResponseThread(e.getActionCommand()).start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 2) {
				int rowNum = commonUserView.getTableVideoProcessTask()
						.getModel().getRowCount() - 1;
				int taskId = Integer.valueOf(commonUserView
						.getTableVideoProcessTask().getValueAt(rowNum, 0)
						.toString());
				String path = videoTasks.get(taskId).getSavePath();
				try {
					Desktop.getDesktop().open(new File(path));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Description :�ڲ��࣬����ť��Ӧ
	 */
	class CommonUserResponseThread extends Thread {
		private String actionCommand;

		public CommonUserResponseThread(String actionCommand) {
			this.actionCommand = actionCommand;
		}

		@Override
		public void run() {
			if (actionCommand.equals("buttonAddVideoProcessTask")) {// �����Ƶ��������ť��Ӧ
				new AddVideoProcessTaskAction(videoTasksManageThread,
						videoTaskIndex);
				videoTaskIndex += 1;
			} else if (actionCommand.equals("buttonUpdate")) {// �޸��û���Ϣ��ť��Ӧ
				int id = user.getId();
				new UpdateUserAction(id, 0, null, 1);
			}

		}
	}

}
