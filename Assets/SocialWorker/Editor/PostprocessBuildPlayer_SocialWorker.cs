using UnityEngine;
using System.Collections;
using System.IO;
using UnityEditor;
using UnityEditor.Callbacks;
using UnityEditor.iOS.Xcode;

public class PostprocessBuildPlayer_SocialWorker
{
	[PostProcessBuild]
	public static void OnPostprocessBuild(BuildTarget buildTarget, string path)
	{
		Debug.Log(string.Format("[Unity - PostProcessBuild] {0}", "SocialWorker"));

		if (buildTarget != BuildTarget.iOS) return;

		string projPath = PBXProject.GetPBXProjectPath(path);

		PBXProject proj = new PBXProject();
		proj.ReadFromFile(projPath);

		string target = proj.TargetGuidByName(PBXProject.GetUnityTargetName());

		proj.AddFrameworkToProject(target, "Social.framework", false);
		proj.AddFrameworkToProject(target, "MessageUI.framework", false);

		proj.WriteToFile(projPath);
	}
}
