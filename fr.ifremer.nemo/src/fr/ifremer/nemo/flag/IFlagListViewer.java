package fr.ifremer.nemo.flag;

public interface IFlagListViewer {
	/**
	 * Update the view to reflect the fact that a task was added 
	 * to the task list
	 * 
	 * @param task
	 */
	public void addFlag(Flag flag);
	
	/**
	 * Update the view to reflect the fact that a task was removed 
	 * from the task list
	 * 
	 * @param task
	 */
	public void removeFlag(Flag flag);
	
	/**
	 * Update the view to reflect the fact that one of the tasks
	 * was modified 
	 * 
	 * @param task
	 */
	public void updateFlag(Flag flag);
}
