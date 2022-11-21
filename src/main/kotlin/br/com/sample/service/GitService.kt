package br.com.sample.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import java.io.File
import java.nio.file.Path

@Service
class GitService(
    @Value("\${git.user}") private val userName: String,
    @Value("\${git.password}") private val password: String,
    @Value("\${git.url}") private val url: String,
    @Value("\${git.path-to-clone}") private val pathToClone: String,
    @Value("\${git.files-to-add}") private val fileToAdd: String

) {
    val credentials = UsernamePasswordCredentialsProvider(userName, password)


    private fun git(): Git{
        return Git.open(File(pathToClone))
    }

    suspend fun start(){
        cloneRepository()
        listBranch().collect(FlowCollector { pushAlterations(it) })
    }
    suspend fun cloneRepository() = withContext(Dispatchers.IO) {
        Git
            .cloneRepository()
            .setURI(url)
            .setDirectory(File(pathToClone)).call()
            .close()//.setCredentialsProvider(credentials).call()
    }

    fun listBranch(): Flow<String> {
        return git().branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().asFlow()
            .map { it.name.replace("refs/remotes/origin/","")}
    }

    suspend fun checkoutToBranch(branchName:String): String= withContext(Dispatchers.IO) {
        return@withContext git()
            .checkout().setName(branchName)
            .setCreateBranch(true)
             .setStartPoint("origin/".plus(branchName)).call().name
    }

    suspend fun pushAlterations(branchName: String){
        val checkout = checkoutToBranch(branchName)
        if(git().repository.branch.equals(checkout)){
            addFile()
            checkBeforeAAdd()
            git().add().addFilepattern(fileToAdd).call()
            commit()
            //git().push().setCredentialsProvider(credentials).call()
            val untracked = git().status().call().untrackedFolders
            if (untracked.isNotEmpty()) git().clean().call()
        }
    }

    private fun commit() {
        val commit = git().commit()
        commit.author = PersonIdent(userName, "email@teste.com")
        commit.message = "teste"
        commit.call()
    }

    private suspend fun checkBeforeAAdd() : Boolean = withContext(Dispatchers.IO){
        val status = git().status().call()
        return@withContext (status.changed.equals(fileToAdd)
                && status.modified.isEmpty()
                && status.untracked.isEmpty()
                && status.conflicting.isEmpty() && status.untrackedFolders.isEmpty())
    }

    suspend fun addFile() = withContext(Dispatchers.IO){
        FileSystemUtils.copyRecursively(Path.of(fileToAdd), Path.of(pathToClone))
    }


}